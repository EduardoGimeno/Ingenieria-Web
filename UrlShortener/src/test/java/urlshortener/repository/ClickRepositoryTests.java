package urlshortener.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import urlshortener.domain.Click;
import urlshortener.fixtures.ClickFixture;
import urlshortener.fixtures.ShortURLFixture;
import urlshortener.repository.impl.ClickRepositoryImpl;
import urlshortener.repository.impl.ShortURLRepositoryImpl;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.sql.Date;

public class ClickRepositoryTests {

    private EmbeddedDatabase db;
    private ClickRepository repository;
    private JdbcTemplate jdbc;

    @Before
    public void setup() {
        db = new EmbeddedDatabaseBuilder().setType(HSQL)
                .addScript("schema-hsqldb.sql").build();
        jdbc = new JdbcTemplate(db);
        ShortURLRepository shortUrlRepository = new ShortURLRepositoryImpl(jdbc);
        shortUrlRepository.save(ShortURLFixture.url1());
        shortUrlRepository.save(ShortURLFixture.url2());
        repository = new ClickRepositoryImpl(jdbc);
    }

    @Test
    public void thatSavePersistsTheClickURL() {
        Click click = repository.save(ClickFixture.click(ShortURLFixture.url1()));
        assertSame(jdbc.queryForObject("select count(*) from CLICK",
                Integer.class), 1);
        assertNotNull(click);
        assertNotNull(click.getId());
    }

    @Test
    public void thatErrorsInSaveReturnsNull() {
        assertNull(repository.save(ClickFixture.click(ShortURLFixture.badUrl())));
        assertSame(jdbc.queryForObject("select count(*) from CLICK",
                Integer.class), 0);
    }

    @Test
    public void thatFindByKeyReturnsAURL() {
        repository.save(ClickFixture.click(ShortURLFixture.url1()));
        repository.save(ClickFixture.click(ShortURLFixture.url2()));
        repository.save(ClickFixture.click(ShortURLFixture.url1()));
        repository.save(ClickFixture.click(ShortURLFixture.url2()));
        repository.save(ClickFixture.click(ShortURLFixture.url1()));
        assertEquals(repository.findByHash(ShortURLFixture.url1().getHash()).size(), 3);
        assertEquals(repository.findByHash(ShortURLFixture.url2().getHash()).size(), 2);
    }

    @Test
    public void thatFindByKeyReturnsEmpty() {
        repository.save(ClickFixture.click(ShortURLFixture.url1()));
        repository.save(ClickFixture.click(ShortURLFixture.url2()));
        repository.save(ClickFixture.click(ShortURLFixture.url1()));
        repository.save(ClickFixture.click(ShortURLFixture.url2()));
        repository.save(ClickFixture.click(ShortURLFixture.url1()));
        assertEquals(repository.findByHash(ShortURLFixture.badUrl().getHash()).size(), 0);
    }

    @Test
    public void thatDeleteDelete() {
        Long id1 = repository.save(ClickFixture.click(ShortURLFixture.url1())).getId();
        Long id2 = repository.save(ClickFixture.click(ShortURLFixture.url2())).getId();
        repository.delete(id1);
        assertEquals(repository.count().intValue(), 1);
        repository.delete(id2);
        assertEquals(repository.count().intValue(), 0);
    }

    //*************** Limitar redirecciones *****************//

    @Test
    public void thatNumberOfClicksCreatedAfterLimit() {
        Date currentDate = new Date(System.currentTimeMillis());
        Long time = currentDate.getTime();
        time = time - 600000;
        Date limit = new Date(time);
        Long redirects = repository.countRedirects(ClickFixture.click(ShortURLFixture.url1()), limit);
        assertEquals(redirects.longValue(), 0);
        repository.save(ClickFixture.click(ShortURLFixture.url1()));
        repository.save(ClickFixture.click(ShortURLFixture.url1()));
        redirects = repository.countRedirects(ClickFixture.click(ShortURLFixture.url1()), limit);
        assertEquals(redirects.longValue(), 2);
    }

    @Test
    public void thatNumberOfClicksCreatedFailsIfTheresNoOneAfterLimit() {
        Date currentDate = new Date(System.currentTimeMillis());
        Long time = currentDate.getTime();
        time = time - 600000;
        Date limit = new Date(time);
        Long redirects = repository.countRedirects(ClickFixture.click(ShortURLFixture.url1()), limit);
        assertEquals(redirects.longValue(), 0);
    }

    @After
    public void shutdown() {
        db.shutdown();
    }

}

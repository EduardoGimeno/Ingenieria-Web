package urlshortener.fixtures;

import java.sql.Date;

import urlshortener.domain.Click;
import urlshortener.domain.ShortURL;

public class ClickFixture {

    public static Click click(ShortURL su) {
        return new Click(null, su.getHash(), new Date(System.currentTimeMillis()), null, null, null, null, null);
    }
}

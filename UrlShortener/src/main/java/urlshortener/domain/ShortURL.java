package urlshortener.domain;

import java.net.URI;
import java.sql.Date;

public class ShortURL {

    private String hash;
    private String target;
    private URI uri;
    private String sponsor;
    private Date created;
    private String owner;
    private Integer mode;
    private Boolean safe;
    private Date t_safe;
    private String ip;
    private String country;
    private Boolean reachable;
    private Date t_reachable;

    public ShortURL(String hash, String target, URI uri, String sponsor,
                    Date created, String owner, Integer mode, Boolean safe, Date t_safe, String ip,
                    String country, Boolean reachable, Date t_reachable) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.sponsor = sponsor;
        this.created = created;
        this.owner = owner;
        this.mode = mode;
        this.safe = safe;
        this.t_safe = t_safe;
        this.ip = ip;
        this.country = country;
        this.reachable = reachable;
        this.t_reachable = t_reachable;
    }

    public ShortURL() {
    }

    public String getHash() {
        return hash;
    }

    public String getTarget() {
        return target;
    }

    public URI getUri() {
        return uri;
    }

    public Date getCreated() {
        return created;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getMode() {
        return mode;
    }

    public String getSponsor() {
        return sponsor;
    }

    public Boolean getSafe() {
        return safe;
    }

    public Date getT_Safe() {
        return t_safe;
    }

    public String getIP() {
        return ip;
    }

    public String getCountry() {
        return country;
    }

    public Boolean getReachable() {
        return reachable;
    }

    public Date getT_Reachable() {
        return t_reachable;
    }
}

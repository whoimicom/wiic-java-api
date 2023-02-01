package kim.kin.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author choky
 */
public class MetaVO implements Serializable {

    private String title;

    private String icon;

    public MetaVO() {

    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public MetaVO(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaVO metaVO)) return false;
        return Objects.equals(getTitle(), metaVO.getTitle()) && Objects.equals(getIcon(), metaVO.getIcon());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getIcon());
    }
}
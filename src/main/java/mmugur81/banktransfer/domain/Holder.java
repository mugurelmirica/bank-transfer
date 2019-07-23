package mmugur81.banktransfer.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.Objects;

/**
 * Owner of a bank account
 */
@Entity
@Getter
@Setter
public class Holder extends BaseEntity {

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Holder)) return false;
        if (!super.equals(o)) return false;
        Holder that = (Holder) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName());
    }
}

package licence.code.generator.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reset_password_token")
@Data
public class ResetPasswordToken {
    private static final int EXPIRATION = 60 * 2;

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String token;

    @PrePersist
    public void autofill() {
        this.setToken(UUID.randomUUID().toString());
    }

    private Date expiryDate;

    //Used by Hibernate
    public ResetPasswordToken() {

    }

    public ResetPasswordToken(final User user) {
        super();

        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public boolean isExpired() {
        return expiryDate.before(new Date());
    }
}

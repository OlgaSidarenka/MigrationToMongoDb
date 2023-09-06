package ua.epam.mishchenko.ticketbooking.model;

import java.math.BigDecimal;

public class UserAccount {

    /**
     * The user account id.
     */
    private Long id;

    /**
     * The amount of user money.
     */
    private BigDecimal money;

    public UserAccount() {
        this.money = BigDecimal.ZERO;
    }

    public UserAccount(BigDecimal money) {
        this.money = money;
    }

    public UserAccount(Long id, BigDecimal money) {
        this.id = id;
        this.money = money;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}

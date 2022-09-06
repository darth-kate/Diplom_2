package serial;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class GetUsersOrdersDeserial {
    private boolean success;
    private List<OrdersDeserial> orders;
    private int total;
    private int totalToday;
}

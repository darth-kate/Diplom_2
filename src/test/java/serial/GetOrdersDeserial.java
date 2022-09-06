package serial;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GetOrdersDeserial {
    private boolean success;
    private List<DataDeserial> data;
}

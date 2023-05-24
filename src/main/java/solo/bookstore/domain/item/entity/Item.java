package solo.bookstore.domain.item.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import solo.bookstore.global.audit.BaseTime;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Item extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_price")
    private BigDecimal itemPrice;

    @Column(name = "item_detail")
    private String itemDetail;

    @Column(name = "item_stock")
    private Long stock;



}

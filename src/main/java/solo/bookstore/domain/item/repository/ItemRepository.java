package solo.bookstore.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solo.bookstore.domain.item.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}

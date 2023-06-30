package antifraud.Card;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface CardRepository extends CrudRepository<Card, Long> {

    boolean existsByNumber(String number);

    @Transactional
    void deleteByNumber(String number);
}

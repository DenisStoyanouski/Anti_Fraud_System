package antifraud.Transaction;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /*There are transactions from 2 regions of the world other than the region of the transaction that is being
    verified in the last hour in the transaction history;*/
    @Query(value = "SELECT COUNT(*) FROM " +
            "(SELECT DISTINCT region FROM transaction WHERE date BETWEEN ?2 AND ?3) " +
            "WHERE region NOT LIKE ?1",
            nativeQuery = true)
    Long countTransactionsFromTwoAnotherRegionsInLastHour(String region, LocalDateTime from, LocalDateTime to);

    /*There are transactions from more than 2 unique IP addresses other than the IP of the transaction that is being
    verified in the last hour in the transaction history.*/
    @Query(value = "SELECT COUNT(*) FROM " +
            "(SELECT DISTINCT ip_address FROM transaction WHERE date BETWEEN ?2 AND ?3) " +
            "WHERE ip_address NOT LIKE ?1",
            nativeQuery = true)
    Long countTransactionsFromTwoAnotherIpAddressInLastHour(String idAddress, LocalDateTime from, LocalDateTime to);
    List<Transaction> findAllByNumber(String number, Sort sort);
    List<Transaction> findAllByFeedbackAndNumber(String feedback, String number, Sort sort);
}

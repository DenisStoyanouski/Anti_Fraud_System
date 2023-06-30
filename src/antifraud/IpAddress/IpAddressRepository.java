package antifraud.IpAddress;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface IpAddressRepository extends CrudRepository<IpAddress, Long> {
    boolean existsByIp(String ip);

    @Transactional
    void deleteByIp(String ip);
}

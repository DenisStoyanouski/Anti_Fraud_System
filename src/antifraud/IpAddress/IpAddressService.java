package antifraud.IpAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IpAddressService {

    private final IpAddressRepository ipAddressRepository;

    @Autowired
    public IpAddressService(IpAddressRepository ipAddressRepository) {
        this.ipAddressRepository = ipAddressRepository;
    }

    public boolean existByIp(String ip) {
        return ipAddressRepository.existsByIp(ip);
    }

}

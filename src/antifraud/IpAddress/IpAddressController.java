package antifraud.IpAddress;

import antifraud.businesslayer.IpAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@RestController
public class IpAddressController {
    @Autowired
    static
    IpAddressRepository ipAddressRepository;

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<IpAddress> addIpAddress(@Valid @RequestBody IpAddress ipAddress) {
        if (ipAddressRepository.existsByIp(ipAddress.getIp())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        ipAddressRepository.save(ipAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(ipAddress);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity removeIpAddress(@PathVariable String ip) {
        if (!IpAddressValidator.isValidIpAddress(ip)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (!ipAddressRepository.existsByIp(ip)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ipAddressRepository.deleteByIp(ip);
        String status = String.format("IP %s successfully removed!", ip);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", status));
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public Iterable<IpAddress> getAllIpAddresses() {
        return ipAddressRepository.findAll();
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            IllegalStateException.class
    })
    public ResponseEntity<Object> handleMethodArgumentAndViolation(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public static IpAddressRepository getIpAddressRepository() {
        return ipAddressRepository;
    }

}

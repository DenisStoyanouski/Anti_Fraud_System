package antifraud.IpAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@RestController
public class IpAddressController {
    @Autowired
    IpAddressRepository ipAddressRepository;

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<IpAddress> addIpAddress(@RequestBody IpAddress ipAddress) {
        if (ipAddressRepository.existsByIp(ipAddress.getIp())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        ipAddressRepository.save(ipAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(ipAddress);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity removeIpAddress(@PathVariable String ip) {
        if (!ip.matches("^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(?!$)|$)){4}$")) {
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
            ConstraintViolationException.class
    })
    public ResponseEntity<Object> handleMethodArgumentAndViolation(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}

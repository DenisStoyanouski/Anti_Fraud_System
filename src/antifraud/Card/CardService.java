package antifraud.Card;

import antifraud.IpAddress.IpAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public boolean existByNumber(String number) {
        return cardRepository.existsByNumber(number);
    }
}

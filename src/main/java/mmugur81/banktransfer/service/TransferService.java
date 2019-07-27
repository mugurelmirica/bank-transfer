package mmugur81.banktransfer.service;

import com.google.inject.ImplementedBy;
import mmugur81.banktransfer.domain.Transfer;
import mmugur81.banktransfer.dto.TransferDto;
import mmugur81.banktransfer.exception.TransferException;

import java.util.Optional;

@ImplementedBy(TransferServiceImpl.class)
public interface TransferService {

    Transfer create(TransferDto dto) throws TransferException;

    void process(Transfer transfer) throws TransferException;

    Optional<Transfer> get(long id);
}

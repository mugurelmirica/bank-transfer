package mmugur81.banktransfer.service;

import com.google.inject.ImplementedBy;
import mmugur81.banktransfer.domain.Holder;

@ImplementedBy(HolderServiceImpl.class)
public interface HolderService {

    Holder create(Holder holder);
}

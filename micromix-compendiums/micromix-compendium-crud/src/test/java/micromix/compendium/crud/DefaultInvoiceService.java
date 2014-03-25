package micromix.compendium.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class DefaultInvoiceService extends SpringDataRepositoryCrudService<InvoiceDto, Invoice, Long, InvoiceRepository, InvoiceQuery> implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public InvoiceRepository dao() {
        return invoiceRepository;
    }

    @Override
    public Invoice convertDtoToEntity(InvoiceDto dto) {
        return new Invoice();
    }

    @Override
    public InvoiceDto convertEntityToDto(Invoice entity) {
        return new InvoiceDto();
    }


    @Override
    public Iterable<InvoiceDto> findAll(Sort orders) {
        return null;
    }

    @Override
    public Page<InvoiceDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<InvoiceDto> findAll() {
        return null;
    }

    @Override
    public Iterable<InvoiceDto> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(InvoiceDto invoiceDto) {

    }

    @Override
    public void delete(Iterable<? extends InvoiceDto> invoiceDtos) {

    }

    @Override
    public void deleteAll() {

    }
}
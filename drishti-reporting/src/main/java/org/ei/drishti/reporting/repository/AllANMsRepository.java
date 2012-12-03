package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.ei.drishti.reporting.domain.ANM.FIND_BY_ANM_ID;

@Repository
public class AllANMsRepository implements ANMCacheableRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllANMsRepository() {
    }

    public AllANMsRepository(DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    @Override
    public void save(ANM objectToBeSaved) {
        dataAccessTemplate.save(objectToBeSaved);
    }

    @Override
    public ANM fetch(ANM objectWhichShouldBeFilledWithMoreInformation) {
        return (ANM) dataAccessTemplate.getUniqueResult(FIND_BY_ANM_ID,
                new String[]{"anmIdentifier"}, new Object[]{objectWhichShouldBeFilledWithMoreInformation.anmIdentifier()});
    }

    @Override
    public List<ANM> fetchAll() {
        return dataAccessTemplate.loadAll(ANM.class);
    }
}

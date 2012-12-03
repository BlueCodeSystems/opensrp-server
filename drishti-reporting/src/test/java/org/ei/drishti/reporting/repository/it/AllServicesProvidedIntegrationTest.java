package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.AllServicesProvidedRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.ei.drishti.reporting.repository.cache.LocationCacheableRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;

public class AllServicesProvidedIntegrationTest extends ServicesProvidedRepositoryIntegrationTestBase {
    @Autowired
    private AllServicesProvidedRepository repository;

    @Autowired
    @Qualifier("serviceProvidedANMRepository")
    private ANMCacheableRepository allANMsRepository;

    @Autowired
    @Qualifier("serviceProvidedDatesRepository")
    private DatesCacheableRepository allDatesRepository;

    @Autowired
    @Qualifier("serviceProvidedIndicatorRepository")
    private IndicatorCacheableRepository allIndicatorsRepository;

    @Autowired
    private LocationCacheableRepository allLocationsRepository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldSaveAService() throws Exception {
        ANM anm = new ANM("ANM X");
        Dates dates = new Dates(LocalDate.now().toDate());
        Indicator indicator = new Indicator("ANC");
        Location location = new Location("Bherya", "Sub Center", "PHC X");
        allANMsRepository.save(anm);
        allDatesRepository.save(dates);
        allIndicatorsRepository.save(indicator);
        allLocationsRepository.save(location);

        Integer anmId = allANMsRepository.fetch(anm).id();
        Integer dateId = allDatesRepository.fetch(dates).id();
        Integer indicatorId = allIndicatorsRepository.fetch(indicator).id();
        Integer locationId = allLocationsRepository.fetch(location).id();

        repository.save(anmId, "123", indicatorId, dateId, locationId);

        assertEquals(1, template.loadAll(ServiceProvided.class).size());
    }
}

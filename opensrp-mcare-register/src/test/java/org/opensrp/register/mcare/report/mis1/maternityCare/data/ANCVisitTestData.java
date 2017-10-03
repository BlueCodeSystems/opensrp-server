package org.opensrp.register.mcare.report.mis1.maternityCare.data;


import org.opensrp.register.mcare.domain.Members;
import org.opensrp.register.mcare.report.mis1.ANCVisit;
import org.opensrp.register.mcare.report.mis1.MIS1TestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.opensrp.register.mcare.domain.Members.BooleanAnswer.NO;
import static org.opensrp.register.mcare.domain.Members.BooleanAnswer.YES;

public class ANCVisitTestData extends MIS1TestData {

    private ANCVisit.ANCVisitBuilder builder;

    public ANCVisitTestData(int totalCount, int validCount, long startDateTime, long endDateTime) {
        super(totalCount, validCount, startDateTime, endDateTime);
        this.builder = new ANCVisit.ANCVisitBuilder();
    }


    public List<Members> createANCVisit1TestData() {
        List<Members> allMembers = new ArrayList<>();

        allMembers.add(createValidMemberWithStartDateTime(VisitNumber.one));
        allMembers.add(createValidMemberWithEndDateTime(VisitNumber.one));

        for (int i = 0; i < totalCount; i++) {
            if (i < validCount - 2) {
                allMembers.add(createValidMemberWithValidRandomDateTime(VisitNumber.one));
            } else {
                allMembers.add(createInValidMemberWithInvalidRandomDateTime(VisitNumber.one));
            }

        }
        return allMembers;
    }

    public List<Members> createANCVisit2TestData() {
        List<Members> allMembers = new ArrayList<>();

        allMembers.add(createValidMemberWithStartDateTime(VisitNumber.two));
        allMembers.add(createValidMemberWithEndDateTime(VisitNumber.two));

        for (int i = 0; i < totalCount; i++) {
            if (i < validCount - 2) {
                allMembers.add(createValidMemberWithValidRandomDateTime(VisitNumber.two));
            } else {
                allMembers.add(createInValidMemberWithInvalidRandomDateTime(VisitNumber.two));
            }

        }
        return allMembers;
    }

    public List<Members> createANCVisitIsReferredData() {
        List<Members> allMembers = new ArrayList<>();


        for (VisitNumber visitNumber : VisitNumber.values()) {
            if(visitNumber != VisitNumber.INVALID) {
                allMembers.add(createValidMemberWithStartDateTime(visitNumber));
                allMembers.add(createValidMemberWithEndDateTime(visitNumber));
            }
        }

        for (int i = 0; i < totalCount; i++) {
            if (i < validCount - 8) {
                allMembers.add(getValidMemberIsReferredBasedOn(i));
            } else {
                allMembers.add(getInvalidMemberBasedOn(i));
            }

        }
        return allMembers;
    }

    public List<Members> createANCVisitMisoprostolReceivedData() {
        List<Members> allMembers = new ArrayList<>();

        allMembers.add(createValidMemberWithStartDateTime(VisitNumber.three));
        allMembers.add(createValidMemberWithEndDateTime(VisitNumber.three));
        allMembers.add(createValidMemberWithStartDateTime(VisitNumber.four));
        allMembers.add(createValidMemberWithEndDateTime(VisitNumber.four));

        allMembers.add(createValidMemberForMisoprostolReceivedUsingBothAnc());

        for (int i = 0; i < totalCount; i++) {
            if (i < validCount - 6) {
                allMembers.add(getValidMemberForMisoprostolReceivedBasedOn(i));
            } else {
                allMembers.add(getInvalidMemberBasedOn(i));
            }

        }
        return allMembers;
    }

    private Members createValidMemberForMisoprostolReceivedUsingBothAnc() {
        builder.clientVersion(startDateTime).misoprostolReceived(YES);
        Map<String , String > visitData = builder.build().getVisitData();
        Members member = createMemberWithANCVisit(VisitNumber.three, visitData);
        member.setANCVisit4(visitData);
        return member;
    }

    private Members createValidMemberWithStartDateTime(VisitNumber visitNumber) {
        builder.clientVersion(startDateTime).isReferred(YES).misoprostolReceived(YES);
        return createMemberWithANCVisit(visitNumber, builder.build().getVisitData());
    }

    private Members createValidMemberWithEndDateTime(VisitNumber visitNumber) {
        builder.clientVersion(endDateTime).isReferred(YES).misoprostolReceived(YES);
        return createMemberWithANCVisit(visitNumber, builder.build().getVisitData());
    }

    private Members getValidMemberIsReferredBasedOn(int count) {
        int mod = count % 4;
        if (mod == 0) {
            return createValidMemberWithValidRandomDateTime(VisitNumber.one);
        } else if (mod == 1) {
            return createValidMemberWithValidRandomDateTime(VisitNumber.two);
        } else if (mod == 2) {
            return createValidMemberWithValidRandomDateTime(VisitNumber.three);
        } else {
            return createValidMemberWithValidRandomDateTime(VisitNumber.four);
        }
    }

    private Members getValidMemberForMisoprostolReceivedBasedOn(int count) {
        int mod = count % 2;
        if (mod == 0) {
            return createValidMemberWithValidRandomDateTime(VisitNumber.three);
        } else {
            return createValidMemberWithValidRandomDateTime(VisitNumber.four);
        }
    }

    private Members getInvalidMemberBasedOn(int count) {
        int mod = count % 2;
        if (mod == 0) {
            int rand = getRandomNumberBetween(0, 4);
            if (rand == 0) {
                return createInValidMemberWithInvalidRandomDateTime(VisitNumber.one);
            } else if (rand == 1) {
                return createInValidMemberWithInvalidRandomDateTime(VisitNumber.two);
            } else if (rand == 2) {
                return createInValidMemberWithInvalidRandomDateTime(VisitNumber.three);
            } else {
                return createInValidMemberWithInvalidRandomDateTime(VisitNumber.four);
            }
        } else {
            int rand = getRandomNumberBetween(0, 4);
            if (rand == 0) {
                return createInValidMemberWithValidRandomDateTime(VisitNumber.one);
            } else if (rand == 1) {
                return createInValidMemberWithValidRandomDateTime(VisitNumber.two);
            } else if (rand == 2) {
                return createInValidMemberWithValidRandomDateTime(VisitNumber.three);
            } else {
                return createInValidMemberWithValidRandomDateTime(VisitNumber.four);
            }
        }
    }

    private Members createValidMemberWithValidRandomDateTime(VisitNumber visitNumber) {
        Long randomDateTimeBetweenStartAndEndDateTime = getRandomNumberBetween(startDateTime, endDateTime);
        builder.clientVersion(randomDateTimeBetweenStartAndEndDateTime).isReferred(YES).misoprostolReceived(YES);
        return createMemberWithANCVisit(visitNumber, builder.build().getVisitData());
    }

    private Members createInValidMemberWithValidRandomDateTime(VisitNumber visitNumber) {
        Long randomDateTimeBetweenStartAndEndDateTime = getRandomNumberBetween(startDateTime, endDateTime);
        builder.clientVersion(randomDateTimeBetweenStartAndEndDateTime).isReferred(NO).misoprostolReceived(NO);
        return createMemberWithANCVisit(visitNumber, builder.build().getVisitData());
    }

    private Members createInValidMemberWithInvalidRandomDateTime(VisitNumber visitNumber) {
        int random = getRandomNumberBetween(1, 100);
        long invalidDataTime;
        if (random % 2 == 0) {
            invalidDataTime = getRandomNumberBetween(0, startDateTime - 1);
        } else {
            invalidDataTime = getRandomNumberBetween(endDateTime + 1, endDateTime * 5);
        }
        builder.clientVersion(invalidDataTime).isReferred(YES).misoprostolReceived(YES);
        return createMemberWithANCVisit(visitNumber, builder.build().getVisitData());
    }


}

open util/boolean as boolean
open util/integer as integer

sig Individual{
    data: one Data,
    healthcareService: one HealthcareService
}

sig Data{
	parametersList: set Int, // < 0 the individual is critical 
//    	accuracy: one Bool
}{
    #parametersList = 1
}

sig HealthcareService {
    notification: some HSNotification
}

sig HSNotification {
	individual: one Individual,
	healthService: one HealthcareService
}

// [D1]
fact dataIsAccurate {
   // all i: Individual | i.data.accuracy = True
}

fact allIndividualsMustHaveDataAssociatedAndCannotBeDataWithoutIndividual {
	(all i:Individual | some d:Data | i.data = d) and
	(all d:Data | some i:Individual | i.data = d)
}

fact allIndividualMustHaveAHealthcareServiceAndCannotBeHealthcareServiceWithoutIndividual {
	(all i:Individual | i.healthcareService != none) and 
	(all h:HealthcareService | some i:Individual | i.healthcareService = h)
}

fact e {
	(all n:HSNotification | some hs: HealthcareService| n.healthService = hs and hs.notification = n) and
	(some h:HealthcareService | some n:HSNotification | h.notification = n and n.healthService = h) and
	(all n:HSNotification | some i:Individual | n.individual = i and i.healthcareService = n.healthService)
}

fact noCommonHSNotificationWithSameHealthService {
	no disj n1,n2:HSNotification | n1.healthService = n2.healthService// and n1.individual = n2.individual
}

fact allIndividualsWhoseDataIsLessThanZeroMustHaveAHSNotification {
	all i:Individual | some p:i.data.parametersList  | p < 0 implies i.healthcareService.notification != none 
										and i.healthcareService.notification.individual = i
}

// compare against defined thresholds
pred checkThresholds[d: Data]{
    // any parameter on an individual's data is below its threshold
    all p:d.parametersList | p < 0
}

assert NotificationToHealthcareServiceSent{
    all i: Individual, h: HealthcareService | some n:h.notification | 
        /*i.data.accuracy = True and*/ checkThresholds[i.data] 
        implies (i.healthcareService = h and h.notification != none and n.individual = i)
}

check NotificationToHealthcareServiceSent

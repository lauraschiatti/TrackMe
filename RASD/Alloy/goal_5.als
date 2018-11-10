open util/integer as integer

sig Individual{
    data: one Data,
    healthcareService: one HealthcareService
}

sig Data {
	parametersList: set Int, // < 0 the individual is critical
}{
    // in order to simplify the things
    #parametersList = 1
}

sig HealthcareService {
    notification: set HSNotification // any number of notifications
}{
	/* All notifications in the set should have an individual related to
	 * the current health care service
	 */
	all n:notification | n.individual.healthcareService = this
}

sig HSNotification {
	individual: one Individual,
	healthService: one HealthcareService
}

/* All individuals must have data associated and all data must be related
 * to an individual.
 * It makes no sense to have data with no relation to an individual
 */
fact allIndividualsMustHaveDataAssociatedAndCannotBeDataWithoutIndividual {
	(all i:Individual | some d:Data | i.data = d) and
	(all d:Data | some i:Individual | i.data = d)
}

/* All individuals must have a relation with a health care service, and all
 * the health care services must be related to an individual.
 * It makes no sense to have a health care service with no relation to an individual
 */
fact allIndividualMustHaveAHealthcareServiceAndCannotBeHealthcareServiceWithoutIndividual {
	(all i:Individual | i.healthcareService != none) and
	(all h:HealthcareService | some i:Individual | i.healthcareService = h)
}

/* All notifications must have a relation with a health care service and an individual.
 * Furthermore, the individual related to the notification must be related to the same
 * health care service the notification has.
 */
fact allNotificationsMustHaveAnAssociatedHealthcareServiceAndIndividual {
	(all n:HSNotification | some hs: HealthcareService| n.healthService = hs) and
	(all n:HSNotification | some i:Individual | n.individual = i and
		i.healthcareService = n.healthService and
		i.healthcareService = n.individual.healthcareService)
}

// There must not be 2 notifications with the same healthcare service
fact noCommonHSNotificationWithSameHealthService {
	no disj n1,n2:HSNotification |
	n1.healthService = n2.healthService and
	n1.individual = n2.individual
}

/* If any parameter in the data of the individual is less than 0, it should have a
 * notification related to it and its health care service.
 */
fact allIndividualsWhoseDataIsLessThanZeroMustHaveAHSNotification {
	(all i:Individual | some p:i.data.parametersList  |
		p < 0 implies i.healthcareService.notification != none
		and i.healthcareService.notification.individual = i
		and i.healthcareService.notification.healthService = i.healthcareService) and
	(all n:HSNotification, i:n.individual | some p:i.data.parametersList | p < 0)
}

// compare against defined thresholds
pred checkThresholds[d: Data]{
    // any parameter on an individual's data is below its threshold
    some p:d.parametersList | p < 0
}

pred show(){
	#Individual > 2
	#HSNotification > 1
}

/* Having an individual with a parameter less than zero, implies that there is a
 * notification related to it and its health care service
 */
assert NotificationToHealthcareServiceSent{
    all i: Individual | checkThresholds[i.data] implies
		(all h:i.healthcareService | some n:i.healthcareService.notification |
		n.healthService = h and n.individual = i)
}


run show

check NotificationToHealthcareServiceSent

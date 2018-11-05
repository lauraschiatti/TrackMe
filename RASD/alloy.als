open util/integer
// open util/boolean

// declares a set Status with 3 elements
abstract sig Status {}
one sig Approved, Pending, Rejected extends Status{}

// declares a set NotificationeType with 2 elements
abstract sig NotificationType{}
one sig ApprovedNotification, RejectedNotification extends NotificationType{}

sig ThirdParty{}

sig Individual{
	requests: some Request
}

sig Request{
	company: one ThirdParty,
	individual: one Individual,
	status: one Status,
	notification: lone NotificationType
}

sig Query{
	company: one ThirdParty,
	individual: one Individual
}

sig QueryResponse{
	query: one Query
}

// an individual is an individual of its request
fact allRequestsShouldBeRelatedToOneIndividualAndMustBeInItsSet {
	(all i:Individual, r:i.requests | r.individual = i) and 
	(some r:Request, i:Individual | r.individual = i)
}

// There should be 1 request per individual and third party. 
// There shouldn't exist 2 requests for the same individual and same third party.
fact noCommonRequestsWithSameIndividualAndSameThirdParty {
	(no disj r1,r2:Request | r1.company = r2.company and r1.individual = r2.individual)
}

// All approved request must have an approved notification
// All rejected request must have a rejected notification
// All pending request must have NO notification
fact  allRequestsShouldHaveAppropiateNotification{
	all r:Request | (r.notification = ApprovedNotification implies r.status = Approved) and
				    (r.notification = RejectedNotification implies (r.status = Rejected)) and
				(r.notification = none implies (r.status = Pending))
}

fact a {
	all qr:QueryResponse, r:qr.query.individual.requests  | 
			(r.status = Approved and 
			r.individual = qr.query.individual and 
			r.company = qr.query.company)
}

assert everyRequestIsInAtMostOneRequestSet {
	all r:Request | lone i:Individual | r in i.requests
}

check everyRequestIsInAtMostOneRequestSet












open util/integer
open trackMeModel

//==========================================================//
//				FACTS

// An individual is an individual of its request
fact allRequestsShouldBeRelatedToOneIndividualAndMustBeInItsSet {
	(all i:Individual, r:i.requests | r.individual = i) and 
	(all r:Request, i:Individual | r.individual = i and r in i.requests)
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

// All queries should have one third party and one individual
// We don't care here if the request was approved or rejected
fact allQueriesShouldBeRelatedToOneRequestRelation {
	all q:Query | some r:q.individual.requests  | 
			(r.individual = q.individual and 
			r.company = q.company)
}

// All query responses should be related to a query with an accepted request
// this means that if the request was rejected, and a third party makes a 
// query asking for an individual's data, there should not be a response
// Otherwise, if the individual approved the request, there should be a 
// response.
fact allQueryResponsesShouldHaveQueryWithAnAcceptedRequest {
	all qr:QueryResponse, q:qr.query | some r:q.individual.requests | 
			(r.status = Approved and
			r.individual = q.individual and 
			r.company = q.company)
}

// There should be 1 query response per query
fact noCommonQueryResponseBetweenTwoQueries {
	no disj qr1,qr2:QueryResponse | qr1.query = qr2.query
}

// Every QeuryResponse should have a query associated to an approved request
fact allQueryResponsesAssociatedToAQueryShouldBeRelatedToAnApprovedRequest {
	some q:Query, qr:QueryResponse | some  r:q.individual.requests | qr.query = q and 
				r.individual = q.individual and
				r.company = q.company and 
				r.status = Approved
}

//==========================================================//
//				PREDICATES

pred show(){
// show worlds with more than 1 request and more than 1 query
	#Request > 1
	#QueryResponse > 1
}

run show

//==========================================================//
//				ASSERTS

assert everyRequestIsInAtMostOneRequestSet {
	all r:Request | lone i:Individual | r in i.requests
}

check everyRequestIsInAtMostOneRequestSet












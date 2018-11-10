open util/integer

//==========================================================//
//				MODEL

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

// All queries that relates with an individual and a company with an accepted request, should have a response
fact  allQueriesWithAnAcceptedRequestRelationShouldHaveAResponse{
	all q:Query,i:q.individual,c:q.company | hasApprovedRequest[i,c] implies 
		( some qr:QueryResponse | qr.query = q and qr.query.company = c and qr.query.individual = i )
}

//==========================================================//
//				PREDICATES

// Check whether the individual has a request that relates him with a company, and it is approved
pred hasApprovedRequest[i:Individual,c:ThirdParty]{
	one r:i.requests | r.company = c and r.individual = i and r.status = Approved
}

pred show(){
// show worlds with more than 2 request, more than 2 queries, and more than 1 query response
	#Request > 2
	#QueryResponse > 1
	#Query > 2
}

//==========================================================//
//				ASSERTS

// Every QueryResponse should be related to an individual and a company with a common accepted request
assert everyAcceptedRequestShouldHaveAResponse {
	all qr:QueryResponse, q:qr.query, i:q.individual, c:q.company | 
		one r:i.requests | 
			r.company = c and r.individual = i and r.status = Approved
}

// 
assert everyRejectedOrPendingRequestShouldNotHaveAResponse {
	all qr:QueryResponse, q:qr.query, i:q.individual, c:q.company | 
		no r:i.requests | 
			r.company = c and r.individual = i and (r.status = Pending or r.status = Rejected)
}

run show

check everyAcceptedRequestShouldHaveAResponse
check everyRejectedOrPendingRequestShouldNotHaveAResponse














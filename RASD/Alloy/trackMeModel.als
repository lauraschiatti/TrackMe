module trackMeModel

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


var express = require('express');
var bodyParser = require('body-parser');
var stringify = require("stringify-object");

var app  = express();

app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true }));

app.post('/data/individual', function (req, res) {
    console.log("Got individual data: " + stringify(req.body));
    res.send("OK");
});

app.post('/data/bulk', function (req, res) {
    console.log("Got bulk data: " + stringify(req.body));
    res.send("OK");
});

app.post('/data/notification', function (req, res) {
    console.log("Got notification: " + stringify(req.body));
    res.send("OK");
});

app.listen(3000, function () {
    console.log('Third party server has started listening for information.');
});
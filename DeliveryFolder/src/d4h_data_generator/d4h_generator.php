#!/usr/bin/php -q

<?php
function random_float ($min,$max) {
    $float = ($min + lcg_value()*(abs($max - $min)));
    return round($float, 6);
}

function generateData(){
    // Get the contents of the JSON file
    $coordinatesFileContent = file_get_contents(__DIR__ . "/coordinates.json");
    // Convert to array
    $individuals = json_decode($coordinatesFileContent, true);

    foreach($individuals as $individual) {
        $ssn = $individual["ssn"];

        $location = array(
            "latitude" => $individual["latitude"],
            "longitude" => $individual["longitude"]
        );

        /** HealthStatus **/
        $heartRate = rand(50, 200);
        $systolic = random_float(108, 140);
        $diastolic = random_float(67, 88);
        $bodyTemperature = random_float(35, 41.5);
        $bloodOxygen = rand(80, 100);

        $healthStatus = array(
            "heartRate" => $heartRate,
            "systolic" => $systolic,
            "diastolic" => $diastolic,
            "bodyTemperature" => $bodyTemperature,
            "bloodOxygen" => $bloodOxygen
        );

        $data = array(
            "ssn" => $ssn,
            "location" => $location,
            "healthStatus" => $healthStatus,
        );

        pushData($data);
    }
}

function pushData($data) {
    //API URL
    $url = 'http://d4h:4567/internal/data';

    //create a new cURL resource
    $ch = curl_init($url);

    //setup request to send json via POST
    $payload = json_encode($data);

    // var_dump($payload);

    //attach encoded JSON string to the POST fields
    curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);

    //set the content type to application/json
    curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));

    //return response instead of outputting
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

    //execute the POST request
    $result = curl_exec($ch);

    if(curl_errno($ch)) {
        echo "error ".curl_error($ch)."\n";
    }

    // $response = curl_exec($ch) ;

    //close URL resource
    curl_close($ch);
}

while (true) {
    generateData();
    sleep(60); // sleep for 60 seconds
}

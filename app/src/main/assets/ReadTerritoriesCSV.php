<?php

$districts = array(); //new HashMap<Integer, String>();
$municipalities = array(); //new HashMap<Integer, String>();
$towns = array(); //new HashMap<String, String>();

$municipalitiesDistrict = array(); //new HashMap<Integer,Integer>();
$townsMunicipalities = array(); //new HashMap<String, Integer>();

$municipalitiesEmails = array(); //new HashMap<String, String>();

$categoriesFile = fopen("categories-ine.csv", "r");
$line = NULL;
$level = NULL;
$code = NULL;
$name = NULL;

while(!feof($categoriesFile)) {
    $line = fgetcsv($categoriesFile);
    $level = $line[0];
    $code = $line[1];
    $name = $line[2];
    
    switch($level) {
        case 1:
            $districts[$code] = $name;
            break;
        case 2:
            $district = substr($code, 0, 2);
            $municipalities[$code] = $name;
            $municipalitiesDistrict[$code] = $district;
            break;
        case 3:
            $municipality = substr($code, 0, 4);
            $towns[$code] = $name;
            $townsMunicipalities[$code] = $municipality;
            break;
        default:
            die("Error on switch\n");
    }
}

fclose($categoriesFile);

$emailsFile = fopen("municipalitiesEmail.csv", "r");

while(! feof($emailsFile)) {
    $line = fgetcsv($emailsFile);
    $municipalityName = $line[0];
    $municipalityEmail = $line[1]; //only getting the first e-mail
    $lowerCaseName = mb_strtolower($municipalityName);
    $municipalitiesEmails[$lowerCaseName] = $municipalityEmail;
}

fclose($emailsFile);


$sqlString = "INSERT INTO District (id, name) VALUES\n";

foreach($districts as $code => $name) {
    $sqlString .= "(" . $code . ",\"" . $name . "\"),\n";
}

$sqlString = rtrim($sqlString, ",\n") . ";\n\n";

$sqlString .= "INSERT INTO Municipality (id, name, email, district) VALUES\n";
foreach($municipalities as $code => $name) {
    $lowerCaseName = mb_strtolower($name);
    $email = $municipalitiesEmails[$lowerCaseName];
    $district = $municipalitiesDistrict[$code];
    
    $sqlString .= "(" . $code . ",\"" . $name . "\",\"" . $email . "\"," . $district . "),\n";
}

$sqlString = rtrim($sqlString, ",\n") . ";\n\n";

$sqlString .= "INSERT INTO Town (id, name, municipality) VALUES\n";
foreach($towns as $code => $name) {
    $municipality = $townsMunicipalities[$code];
    
    $sqlString .= "(\"" . $code . "\",\"" . $name . "\"," . $municipality . "),\n";
}

$sqlString = rtrim($sqlString, ",\n") . ";\n\n";

$insertFile = fopen("territorial_insert.sql", "w");
fwrite($insertFile, $sqlString);
fclose($insertFile);

?>

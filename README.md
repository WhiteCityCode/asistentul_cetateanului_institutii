# Asistentul cetățeanului - Instituții

## Scop

Aplicația este un API endpoint pentru instituțiile statului care le permite acestora să trimită notificări în sistemul *Asistentul cetățeanului*. Fiecare instituție își poate crea un provider care poate să aibă una sau mai multe aplicații care pot trimite un singur tip de notificări.

## Rulare

Aplicația este scrisă în Java cu Spring Boot. Pentru împachetare/rulare este nevoie de Maven. Rulați `mvn package` pentru crearea unui pachet *.jar care poate fi apoi rulat cu `java -jar target/jar-file-SNAPSHOT.jar`. 

Aplicația așteaptă ca un fișier `src/main/resources/application.properties` să fie prezent și să conțină datele de configurare ale aplicației. Fișierul poate fi obținut prin redenumirea fișierului `src/main/resources/application.properties.dist` și editarea conținutului cu date de configurare reale pentru bazele de date.

## Securitate

Pentru accesarea endpointurilor API-ului ese necesar un token JWT care se poate obtine la ruta `/api/auth` postand un obiect json cu `email` si `password` avand drept conditie aceea ca valoarea campului email sa fie la fel cu adresa de email a unui user din DB. Cu acest token se pot accesa rutele API-ului in conformitate cu rolurile.

## Contribuții

1. înscriere pe [portalul voluntarilor](https://voluntari.ithub.gov.ro/) și completarea profilului
2. înscriere pe [canalul de Slack](https://govithub.slack.com/)
3. join la #asistentul_cetateanului si la #asistentul_cetateanului_be
4. request catre @claudiu pentru acces la Confluence si Jira, determinare skillset si alegere task
5. have fun
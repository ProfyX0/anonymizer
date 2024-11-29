Riešil som úlohu BigData/logy.
Rozhodol som sa pre Javu, špecificky Spring, kvôli mnohým frameworkom, ktoré mi môžu uľahčit implementáciu. Ale je vysoko pravdepodobné, že takáto aplikácia bude pomalšia v porovnaní s takou, ktorá by bola napisaná v napr. C++.


In hindsight, mal som si spraviť väčší research ohľadne CapnProto decodingu, hlavne, že či existuje modul pre Javu predtým, ako som začal niečo implementovať. Na začiatku som sa totiž rovno pustil do konfigurácie Kafka Listeneru bez presnejších informácii o CapnProto. Pomocou capnproto-java (https://github.com/capnproto/capnproto-java/) som transformoval .capnp na Httplog.java.

Pre komunikáciu s databázou (resp. proxy) som najprv použil JDBCtemplate s manuálne-nakonfigurovaným datasource, alternatívou bol JPA, ale nebol som si úplne istý, či nebude treba meniť nejaké additional properties datasourcu + väčsia kontrola pre prípadný debug, zhľadiska performance by mali byť +- rovnaké, mierne lepšie JDBC.

Keďže databázu môžem accessnúť cez proxy iba raz za minútu, je potrebné mať implementovaný nejaký local cache, ktorý bude odchytávať 
HTTP requesty od Kafky a priebežne ich bude mať uložený v dátovej štruktúre. Teoreticky by mohli byť uložené v nejakej inej databáze a to by vyriešilo problém straty dát v niektorých prípadoch, ale bolo by to výrazne pomalšie. Dátová štruktúra, ktorú som zvolil je concurrent linked list, čo je fast thread-safe štruktúra. Z tohto localCache sa raz za 65s pošle scheduled task, ktorý zoberie všetky doterajšie requesty z localCache a pošle ich do Clickhousu.

Pri testovaní a debuggingu som ale natrafil na problém s tým, že JDBCtemplate posiela 2 HTTP requesty, napriek tomu, že naprogramovaný bol iba 1 (per minute). Pôvodne som si myslel, že ide o problém s otváranim a zatváraním connections, ale ani po použití SingleConnectionDataSource (1 connection for the whole time, does not close) sa mi problém nepodarilo opraviť. Debuggoval som preto spojenie s pomocou Wiresharku na ch-proxy a zistil, že ide o pravdepodobne o nejaký health check (SELECT ... WHERE 0). Ani po preštudovaní dokumentácie som neprišiel na to, ako ho vypnúť.
Preto som zmenil logiku a namiesto JDBC (ktorý bol pravdepodobne mierny overkill) som použil modul Apache HTTP client, pri ktorom nevzniká problém takéhoto checku.

Kód je relatívne rozumne a prehľadne napísany (aspoň podľa mňa :D), spĺňa Java konvencie.

Kebyže má ísť do produkcie, tak by sa musel pridať nejaký security layer pri HTTP requestoch (napr. SSL).

Total time:
Research: 25h
Implementácia: 15h
Debug: 20h



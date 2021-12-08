# Accounting

This service parses accounting statements given in csv format and creates monthly and annual reports 
accordingly. The service expects for a monthly report a checking account and credit account and calculates expenses 
and income, neglecting transfers, if corresponding client ids have been configured (see section 'Configuration' for 
more infos).

## Formats

### Source csv format

In the following, two examples are shown of how the processable source csvs do look like.

Checking account statement:
```
"account number:";"XX01234567890123456789 / checking account";

"from:";"01.01.2020";
"till:";"31.01.2020";
"balance from 31.01.2020:";"1.234,56 EUR";

"Buchungstag";"Wertstellung";"Buchungstext";"Auftraggeber / Begünstigter";"Verwendungszweck";"Kontonummer";"BLZ";"Betrag (EUR)";"Gläubiger-ID";"Mandatsreferenz";"Kundenreferenz";
"31.01.2020";"31.01.2020";"Überweisung";"XXX VISACARD";"9876543210987645 08.39  PETER LUSTIG  XXX INTERNET BANKING                  DATUM 31.01.2020, 08.39 UHR";"XX98765432109876543210";"YYXXCCV9999";"-100,00";"";"";"NOTPROVIDED";
"30.01.2020";"30.01.2020";"Gutschrift";"COMDIRECT KONTOAUFLOESUNG";"RESTGUTHABEN KTO 0716562400 BLZ 20041155";"DE54200411550716562400";"COBADEHD055";"1,02";"";"";"NOTPROVIDED";
"28.01.2020";"28.01.2020";"Überweisung";"XXX VISACARD";"9876543210987645 09.40  PETER LUSTIG  XXX INTERNET BANKING                  DATUM 28.01.2020, 09.40 UHR";"XX98765432109876543210";"YYXXCCV9999";"-100,00";"";"";"NOTPROVIDED";
"27.01.2020";"27.01.2020";"Überweisung";"XXX VISACARD";"9876543210987645 02.37  PETER LUSTIG  XXX INTERNET BANKING                  DATUM 26.01.2020, 02.37 UHR";"XX98765432109876543210";"YYXXCCV9999";"-200,00";"";"";"NOTPROVIDED";
"24.01.2020";"24.01.2020";"Dauerauftrag";"YXZ BLUB REVOLUTION LTD. CO. KG";"7894/5/789-123";"XX45698712304569871230";"ZZYYXX9WECXX";"-590,00";"";"";"NOTPROVIDED";
"24.01.2020";"24.01.2020";"Dauerauftrag";"PRIMA GMBH";"Kunden-Nr 11111111";"YY45698712398745698700";"YYYXXXZZZ000";"-36,00";"";"";"NOTPROVIDED";
"24.01.2020";"24.01.2020";"";"KREDITKARTENABRECHNUNG";"VISA-ABR. 987000XXXXXX1234";"1004567898";"98060000";"-28,20";"";"";"";
"22.01.2020";"22.01.2020";"Lastschrift";"SumPay (Europ) X.x.x.x at al. A.B.C.";"XX.1199.YY . ACME, Ihr Einkauf bei ACME";"PP77777755555566666111";"ZULUFUXCXCC";"-62,96";"FU55YYY1111111111111111199                       ";"9X99999XVC456         ";"";
"22.01.2020";"22.01.2020";"Lastschrift";"OH Distribute Ltd.";"Ticket YXZYXZ";"ZZ88888888882222222222";"ZUZUNBNBZZZ";"-33,00";"DE45678941654898198    ";"8700008700008700      ";"";
"22.01.2020";"22.01.2020";"Lastschrift";"GETRICH FINANCIAL SERVICES GMBH";"Vielen Dank fuer Ihren Einkauf ueber die Awesome-App.";"BB45698418984894984848";"GFGFGFGFGFGF";"-2,90";"PO7894161567968216    ";"8080808080            ";"";
"21.01.2020";"21.01.2020";"Lastschrift";"MONSTER US B.L.U.B., SOMEWHERE NICE";"111-5555555-8888888 Creepy.de VC87DF54WQ988888";"DE78975464123195915373";"PULENXXX";"-125,00";"RE7887877878787878    ";"3489o.)490fkdl04lksfd04kfsl).kfdd                   ";"";
"16.01.2020";"16.01.2020";"Kartenzahlung/-abrechnung";"SUPER 8888, BERLIN//BERLIN/DE / SUPER 8888, BERLIN";"2020-01-15T20:01 7807806Debitk.1 456";"BV45678945648945645899";"UZUUTHGRTGFB";"-32,79";"";"";"78998778964545645612332132";
"16.01.2020";"16.01.2020";"Lastschrift";"SumPay (Europ) X.x.x.x at al. A.B.C.";"XX.1199.YY . SALVATORAPO, Ihr Einkauf bei SALVATORAPO";"PP77777755555566666111";"ZULUFUXCXCC";"-15,27";"FU55YYY1111111111111111199                       ";"9X99999XVC456         ";"";
"15.01.2020";"15.01.2020";"Lastschrift";"OH Distribute Ltd.";"Ticket YXZYXZ";"ZZ88888888882222222222";"ZUZUNBNBZZZ";"-2,70";"DE45678941654898198    ";"8700008700008700      ";"";
"14.01.2020";"14.01.2020";"Lastschrift";"Awesome Stuff Ltd.";"X2342344 Z987987545 Y789456123 PremiumStuff";"CV45645678879987456456";"WESDFDJK456";"-9,99";"BU7894567894563215    ";"5SDFOJIJOISDSJFDOI9458SKJFOD                      ";"";
"13.01.2020";"13.01.2020";"Gutschrift";"YXZ BLUB REVOLUTION GmbH";"736 230,50 7777/5/999 mark.Bua. Beko.-Abrechnung";"XX45698712304569871230";"ZZYYXX9WECXX";"230,50";"";"";"NOTPROVIDED";
"13.01.2020";"13.01.2020";"Überweisung";"XXX VISACARD";"9876543210987645 18.22  PETER LUSTIG  XXX INTERNET BANKING                  DATUM 10.01.2020, 18.22 UHR";"XX98765432109876543210";"YYXXCCV9999";"-150,00";"";"";"";
"10.01.2020";"10.01.2020";"Lastschrift";"MONSTER US B.L.U.B., SOMEWHERE NICE";"111-5555555-8888888 Creepy.de VC87DF54WQ988888";"DE78975464123195915373";"PULENXXX";"-12,90";"RE7887877878787878    ";"3489o.)490fkdl04lksfd04kfsl).kfdd                   ";"";
"09.01.2020";"09.01.2020";"Lastschrift";"SumPay (Europ) X.x.x.x at al. A.B.C.";"XX.1199.YY . 20XX, Ihr Einkauf bei 20XX";"PP77777755555566666111";"ZULUFUXCXCC";"-33,89";"FU55YYY1111111111111111199                       ";"9X99999XVC456         ";"";
"08.01.2020";"08.01.2020";"Lastschrift";"MONSTER US B.L.U.B., SOMEWHERE NICE";"111-5555555-8888888 Creepy.de VC87DF54WQ988888";"DE78975464123195915373";"PULENXXX";"-9,99";"RE7887877878787878    ";"3489o.)490fkdl04lksfd04kfsl).kfdd                   ";"";
"08.01.2020";"08.01.2020";"Lastschrift";"MONSTER US B.L.U.B., SOMEWHERE NICE";"111-5555555-8888888 Creepy.de VC87DF54WQ988888";"DE78975464123195915373";"PULENXXX";"-26,08";"RE7887877878787878    ";"3489o.)490fkdl04lksfd04kfsl).kfdd                   ";"";
"07.01.2020";"07.01.2020";"Lastschrift";"SumPay (Europ) X.x.x.x at al. A.B.C.";". SPOTIFY, Ihr Einkauf bei SPOTIFY";"PP77777755555566666111";"ZULUFUXCXCC";"-9,99";"FU55YYY1111111111111111199                       ";"9X99999XVC456         ";"";
"07.01.2020";"07.01.2020";"Lastschrift";"SumPay (Europ) X.x.x.x at al. A.B.C.";". CINEMAXX, Ihr Einkauf bei CINEMAXX";"PP77777755555566666111";"ZULUFUXCXCC";"-12,20";"FU55YYY1111111111111111199                       ";"9X99999XVC456         ";"";
"07.01.2020";"07.01.2020";"Lastschrift";"SumPay (Europ) X.x.x.x at al. A.B.C.";"XX.1199.YY . MOMOX GMBH, Ihr Einkauf bei MOMOX GMBH";"PP77777755555566666111";"ZULUFUXCXCC";"-25,96";"FU55YYY1111111111111111199                       ";"9X99999XVC456         ";"";
"07.01.2020";"07.01.2020";"Gutschrift";"John Doe";"Somethin  monatlich Peter";"DE15915915975375375333";"YYXXCCV9999";"8,00";"";"";"Somethin  monatlich Peter";
"06.01.2020";"06.01.2020";"Lastschrift";"SumPay (Europ) X.x.x.x at al. A.B.C.";"XX.1199.YY . MOMOX GMBH, Ihr Einkauf bei MOMOX GMBH";"PP77777755555566666111";"ZULUFUXCXCC";"-10,90";"FU55YYY1111111111111111199                       ";"9X99999XVC456         ";"";
"06.01.2020";"06.01.2020";"Lastschrift";"OH Distribute Ltd.";"Ticket YXZYXZ";"ZZ88888888882222222222";"ZUZUNBNBZZZ";"-33,00";"DE45678941654898198    ";"8700008700008700      ";"";
"06.01.2020";"06.01.2020";"Lastschrift";"And Even More Stuff Service Bla.";"KD-NR . 88887777889, RG-NR . 404040404, Faelligkeit 06 . 01 . 2020, Mandats-ID LK789456456-999, Glaeubiger-ID IU78945679854679823";"KO77777777777788888888";"ZUZUNBNBZZZ";"-27,50";"IU78945679854679823    ";"LK789456456-999        ";"";
"03.01.2020";"03.01.2020";"Überweisung";"XXX VISACARD";"9876543210987645 15.46  PETER LUSTIG  XXX INTERNET BANKING                  DATUM 03.01.2020, 15.46 UHR";"XX98765432109876543210";"YYXXCCV9999";"-300,00";"";"";"NOTPROVIDED";
"03.01.2020";"03.01.2020";"Lastschrift";"OH Distribute Ltd.";"Ticket YXZYXZ";"ZZ88888888882222222222";"ZUZUNBNBZZZ";"-10,71";"DE45678941654898198    ";"8700008700008700      ";"";
"02.01.2020";"02.01.2020";"Lastschrift";"Go Aroung Come Around (GACA)";"/RFB/P0000000000/0001, 02.01.2222,42Sub Rate PremiumSubscription 68482646 XXYYX+Blubber Enterprises";"TR33333333333333333335";"BELA66600000";"-63,42";"DA55555555557777778    ";"0001-900000000000     ";"";
```

Credit Card statement:
```
"credit card:";"1111********9999";

"from:";"01.01.2020";
"till:";"31.01.2020";
"balance:";"108.39 EUR";
"date:";"29.02.2020";

"Umsatz abgerechnet und nicht im Saldo enthalten";"Wertstellung";"Belegdatum";"Beschreibung";"Betrag (EUR)";"Urspr�nglicher Betrag";
"Ja";"01.02.2020";"30.01.2020";"FOOBAR.COM 7899877898Somewhere";"-12,72";"-14,99 USD";
"Ja";"01.02.2020";"30.01.2020";"YetAnother MarktLaLaLand";"-11,66";"";
"Ja";"01.02.2020";"26.01.2020";"FOOBAR PURCHASESEATTLE";"-7,48";"";
"Ja";"30.01.2020";"28.01.2020";"Alnatura Produktions u.LaLaLand";"-4,01";"";
"Ja";"30.01.2020";"29.01.2020";"Transact,Invaliden 31LaLaLand";"-50,00";"";
"Ja";"30.01.2020";"28.01.2020";"Station Station 123456789LaLaLand";"-9,47";"";
"Ja";"30.01.2020";"29.01.2020";"FOOBAR.COM 7899877898Somewhere";"-63,46";"";
"Ja";"25.01.2020";"24.01.2020";"BLABLA Markt GmbH-ZwLaLaLand";"-40,51";"";
"Ja";"23.01.2020";"21.01.2020";"YetAnother MarktLaLaLand";"-18,47";"";
"Ja";"23.01.2020";"21.01.2020";"BLABLA Markt GmbH-ZwLaLaLand";"-14,27";"";
"Ja";"23.01.2020";"23.01.2020";"Einzahlung";"200,00";"";
"Ja";"21.01.2020";"20.01.2020";"Ausgleich Kreditkarte gem. Abrechnung v. 20.01.20";"38,52";"";
"Ja";"20.01.2020";"19.01.2020";"BLABLA Markt GmbH-ZwLaLaLand";"-27,90";"";
"Ja";"19.01.2020";"18.01.2020";"Transact,Invaliden 31LaLaLand";"-50,00";"";
"Ja";"19.01.2020";"17.01.2020";"DUSSMANN D.KULTURKAUFHBERLIN";"-16,50";"";
"Ja";"17.01.2020";"16.01.2020";"FOOBAR.COM 7899877898Somewhere";"-33,38";"";
"Ja";"16.01.2020";"13.01.2020";"BLABLA Markt GmbH-ZwLaLaLand";"-39,78";"";
"Ja";"16.01.2020";"14.01.2020";"FOOBAR.COM 7899877898Somewhere";"-23,99";"";
"Ja";"16.01.2020";"15.01.2020";"FOOBAR.COM 7899877898Somewhere";"-13,99";"";
"Ja";"16.01.2020";"16.01.2020";"FOOBAR.COM 7899877898Somewhere";"-4,74";"";
"Ja";"12.01.2020";"12.01.2020";"Einzahlung";"100,00";"";
"Ja";"12.01.2020";"12.01.2020";"Einzahlung";"110,00";"";
"Ja";"09.01.2020";"06.01.2020";"Mars ElectroLaLaLand";"-12,99";"";
"Ja";"09.01.2020";"07.01.2020";"Transact,GA 7184LaLaLand";"-50,00";"";
"Ja";"09.01.2020";"07.01.2020";"BLABLA Markt GmbH-ZwLaLaLand";"-28,46";"";
"Ja";"06.01.2020";"05.01.2020";"Blub Produktions u.LaLaLand";"-10,85";"";
"Ja";"06.01.2020";"03.01.2020";"FOOBAR PURCHASESEATTLE";"-16,79";"";
"Ja";"04.01.2020";"02.01.2020";"CULTURE LALALAND";"-13,99";"";
"Ja";"04.01.2020";"01.01.2020";"FOOBAR PURCHASESEATTLE";"-1,63";"";
"Ja";"04.01.2020";"01.01.2020";"FOOBAR PURCHASESEATTLE";"-3,49";"";
"Ja";"03.01.2020";"02.01.2020";"BLABLA Markt GmbH-ZwLaLaLand";"-21,73";"";
"Ja";"03.01.2020";"02.01.2020";"Mars ElectroLaLaLand";"-66,68";"";
"Ja";"02.01.2020";"02.01.2020";"Einzahlung";"150,00";"";
```

### Target csv report

The service a single target CSV report file, which looks similar to the following example:

```
ID,Year,Month,Income,Expenditure,Win,SavingRate,####,Accommodation,Food,Health,Transportation,Leisure,Misc
2020_01_CHECKING_ACCOUNT,2020,JANUARY,"1000,13","-505,54","494,59","49,45",----,"-250,43","-100,95",0,"-47,95","-34,12","-62,68"
2020_02_CHECKING_ACCOUNT,2020,FEBRUARY,"1001,13","-504,54","496,59","49,60",----,"-249,43","-99,95",0,"-46,95","-33,12","-61,68"
2020_03_CHECKING_ACCOUNT,2020,MARCH,"1002,13","-503,54","498,59","49,75",----,"-248,43","-98,95",0,"-45,95","-32,12","-60,68"
2020_04_CHECKING_ACCOUNT,2020,APRIL,"1003,13","-502,54","500,59","49,90",----,"-247,43","-97,95",0,"-44,95","-31,12","-59,68"
2020_05_CHECKING_ACCOUNT,2020,MAY,"1004,13","-501,54","502,59","50,05",----,"-246,43","-96,95",0,"-43,95","-30,12","-58,68"
2020_06_CHECKING_ACCOUNT,2020,JUNE,"1005,13","-500,54","504,59","50,20",----,"-245,43","-95,95",0,"-42,95","-29,12","-57,68"
2020_07_CHECKING_ACCOUNT,2020,JULY,"1006,13","-499,54","506,59","50,35",----,"-244,43","-94,95",0,"-41,95","-28,12","-56,68"
2020_08_CHECKING_ACCOUNT,2020,AUGUST,"1007,13","-498,54","508,59","50,50",----,"-243,43","-93,95",0,"-40,95","-27,12","-55,68"
2020_09_CHECKING_ACCOUNT,2020,SEPTEMBER,"1008,13","-497,54","510,59","50,65",----,"-242,43","-92,95",0,"-39,95","-26,12","-54,68"
2020_10_CHECKING_ACCOUNT,2020,OCTOBER,"1009,13","-496,54","512,59","50,80",----,"-241,43","-91,95",0,"-38,95","-25,12","-53,68"
2020_11_CHECKING_ACCOUNT,2020,NOVEMBER,"1010,13","-495,54","514,59","50,94",----,"-240,43","-90,95",0,"-37,95","-24,12","-52,68"
2020_12_CHECKING_ACCOUNT,2020,DECEMBER,"1011,13","-494,54","516,59","51,09",----,"-239,43","-89,95",0,"-36,95","-23,12","-51,68"
2021_01_CHECKING_ACCOUNT,2021,JANUARY,"1012,13","-493,54","518,59","51,24",----,"-238,43","-88,95",0,"-35,95","-22,12","-50,68"
2021_02_CHECKING_ACCOUNT,2021,FEBRUARY,"1013,13","-492,54","520,59","51,38",----,"-237,43","-87,95",0,"-34,95","-21,12","-49,68"
2021_03_CHECKING_ACCOUNT,2021,MARCH,"1014,13","-491,54","522,59","51,53",----,"-236,43","-86,95",0,"-33,95","-20,12","-48,68"
2021_04_CHECKING_ACCOUNT,2021,APRIL,"1015,13","-490,54","524,59","51,68",----,"-235,43","-85,95",0,"-32,95","-19,12","-47,68"
,,,,,,,,,,,,,
2020_ANNUAL_REPORT,2020,----,"12067,56","-6000,48","6067,08","50,28",,,,,,,
2021_ANNUAL_REPORT,2021,----,"4054,52","-1968,16","2086,36","51,46",,,,,,,
```

## Configuration

Place an application.properties file next to the jar file containing the following configuration:

```
# optional; can be provided by cli at runtime
app.csvPath=/path/to/csv/dir 

# the following two IDs are Strings used to distinguish between checking account and credit card
# the service looks for these in the first line of the given csv file
app.checkingAccountIdentifier=CheckingAccount
app.creditCardIdentifier=CreditCard

# optional; a list of Strings which are used as IDs for mapping statements entries to cost centres
app.expense.accommodation=rent,electricity
app.expense.food=Awesome Supermarket X
app.expense.health=
app.expense.transportation=BVG
app.expense.purchases=amazon,spotify

# IDs used to identify transfers that affect one's own account to avoid falsy profit and expentiture calculation
app.ownTransferIdentifiers=credit card,Deposit

# IDs used to signify that statement entries containing the listed clients should look in the 'intendedUse' field 
for the cost centre mapping
app.intendedUseIdentifiers=PayPal
```

## Start the service

```
$ java -jar accounting.jar
```
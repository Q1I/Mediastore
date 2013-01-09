
CREATE TABLE Produkt(	
	P_ID  CHAR(10) NOT NULL,
	Titel VARCHAR(120) NOT NULL,
	Verkaufsrang INT,
	Bild VARCHAR(150),
	PRIMARY KEY(P_ID)
);

CREATE TABLE Aehnlich(  
	Produkt_ID CHAR(10) NOT NULL,
	Aehnlich_ID CHAR(10) NOT NULL,
	PRIMARY KEY(Produkt_ID , Aehnlich_ID),
	FOREIGN KEY(Produkt_ID) REFERENCES Produkt
	ON DELETE CASCADE,
	FOREIGN KEY (Aehnlich_ID) REFERENCES Produkt
	ON DELETE CASCADE
);

CREATE TABLE Kategorie(		
	Kategorie_ID VARCHAR(200) NOT NULL,
	Name VARCHAR(50) NOT NULL,
	PRIMARY KEY(Kategorie_ID)
);

              
CREATE TABLE Produkt_Kategorie(	
	Produkt_ID CHAR(10) NOT NULL,
	Kategorie_ID VARCHAR(200) NOT NULL,
	PRIMARY KEY(Produkt_ID, Kategorie_ID),
	FOREIGN KEY(Produkt_ID) REFERENCES Produkt
	ON DELETE CASCADE,
	FOREIGN KEY(Kategorie_ID) REFERENCES Kategorie
	ON DELETE CASCADE
);


CREATE TABLE Oberkategorie(
	Kategorie_ID VARCHAR(200) NOT NULL,
	Oberkategorie_ID VARCHAR(200),
	PRIMARY KEY(Kategorie_ID),
	FOREIGN KEY(Kategorie_ID ) REFERENCES Kategorie
	ON DELETE CASCADE,
	FOREIGN KEY(Oberkategorie_ID) REFERENCES Kategorie
 	ON DELETE set null
);

CREATE TABLE Hauptkategorie(
	Hauptkategorie_ID VARCHAR(200) NOT NULL,
	Unterkategorie_ID VARCHAR(200) NOT NULL,
	PRIMARY KEY(Hauptkategorie_ID, Unterkategorie_ID),
	FOREIGN KEY(Hauptkategorie_ID ) REFERENCES Kategorie
	ON DELETE CASCADE,
	FOREIGN KEY(Unterkategorie_ID) REFERENCES Kategorie
 	ON DELETE CASCADE
);


CREATE TABLE DVD(
	Produkt_ID CHAR(10) NOT NULL,		
	Laufzeit INT NOT NULL,
	Format VARCHAR(100) NOT NULL,
	Regioncode VARCHAR(10),
	PRIMARY KEY(Produkt_ID),
	FOREIGN KEY(Produkt_ID) REFERENCES Produkt
	ON DELETE CASCADE
);

CREATE TABLE Musik(
	Produkt_ID CHAR(10) NOT NULL,
	Label VARCHAR(50) NOT NULL,
	Erscheinungsjahr int default 0,
	PRIMARY KEY(Produkt_ID),
	FOREIGN KEY(Produkt_ID) REFERENCES Produkt
	ON DELETE CASCADE
);

CREATE TABLE Buch (	
	Produkt_ID CHAR(10) NOT NULL,
	ISBN CHAR(13) NOT NULL,
	Erscheinungsdatum DATE default null,
	Seitenzahl INT CHECK (Seitenzahl > 0),
	Verlag VARCHAR(50) NOT NULL,
	PRIMARY KEY(Produkt_ID),
	FOREIGN KEY(Produkt_ID) REFERENCES Produkt
	ON DELETE CASCADE
);

CREATE TABLE Medienperson(	
	Name VARCHAR(80)NOT NULL,
	PRIMARY KEY(Name)
);

CREATE TABLE Buch_Autor (	
	Produkt_ID CHAR(10) NOT NULL,
	Name VARCHAR(80) NOT NULL,
	PRIMARY KEY( Produkt_ID, Name),
	FOREIGN KEY(Name) REFERENCES Medienperson
	ON DELETE CASCADE,
	FOREIGN KEY(Produkt_ID) REFERENCES Buch
	ON DELETE CASCADE
);

CREATE TABLE Musik_Kuenstler ( 
	Produkt_ID CHAR(10) NOT NULL,
	Name VARCHAR(80)NOT NULL,
	PRIMARY KEY(Produkt_ID, Name),
	FOREIGN KEY(Name) REFERENCES Medienperson
	ON DELETE CASCADE,
	FOREIGN KEY(Produkt_ID) REFERENCES Musik
	ON DELETE CASCADE
);

CREATE TABLE DVD_Beteiligung(
	Produkt_ID CHAR(10) NOT NULL,
	Name VARCHAR(80) NOT NULL,
	Rolle VARCHAR(50) NOT NULL,
	PRIMARY KEY( Produkt_ID, Name, Rolle),
	FOREIGN KEY(Name) REFERENCES Medienperson
	ON DELETE CASCADE,
	FOREIGN KEY(Produkt_ID) REFERENCES DVD
	ON DELETE CASCADE
);

CREATE TABLE Lieder(
	Produkt_ID CHAR(10) NOT NULL,
	Name VARCHAR(150) NOT NULL,
	PRIMARY KEY(Produkt_ID, Name),
	FOREIGN KEY(Produkt_ID) REFERENCES Musik
	ON DELETE CASCADE
);

CREATE TABLE Kunde (	
	K_ID VARCHAR(20) NOT NULL,
	Lieferadresse VARCHAR(50),
	Konto INT ,
	Name VARCHAR(50) ,
	Geburtsdatum DATE ,
	PRIMARY KEY(K_ID)
);

CREATE TABLE Filiale (
	F_ID VARCHAR(20) NOT NULL,
	Primary KEY(F_ID)
);

CREATE TABLE Kauf (	
	Kunden_ID VARCHAR(20) NOT NULL,
	Filial_ID VARCHAR(20) NOT NULL,
	Produkt_ID CHAR(10) NOT NULL,
	Zeitstempel TIMESTAMP NOT NULL,
	Preis FLOAT NOT NULL,
	Menge INT NOT NULL,
	Ort VARCHAR(30) NOT NULL, 	
	Strasse VARCHAR(60) NOT NULL,
	PLZ INT NOT NULL,
	PRIMARY KEY(Kunden_ID, Filial_ID, Produkt_ID, Zeitstempel),
	FOREIGN KEY(Kunden_ID) REFERENCES Kunde
	ON DELETE NO ACTION,
	FOREIGN KEY(Produkt_ID) REFERENCES Produkt
	ON DELETE NO ACTION,
	FOREIGN KEY(Filial_ID) REFERENCES Filiale
	ON DELETE NO ACTION
);				


CREATE TABLE Angebot (	
	Filial_ID VARCHAR(20) NOT NULL,
	Produkt_ID CHAR(10) NOT NULL,
	Zustand VARCHAR(20) NOT NULL,
	Preis FLOAT NOT NULL,
	Verfuegbarkeit VARCHAR(20),
	PRIMARY KEY (Filial_ID, Produkt_ID, Preis),
	FOREIGN KEY (Filial_ID) REFERENCES Filiale
	ON DELETE CASCADE,
	FOREIGN KEY (Produkt_ID) REFERENCES Produkt
	ON DELETE CASCADE
);



CREATE TABLE Review (	
	R_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, 
	Produkt_ID CHAR(10) NOT NULL,
	Kunden_ID VARCHAR(20) NOT NULL,
	Punkte INT DEFAULT 0 CHECK (Punkte  BETWEEN 0 AND 5),
	Rezension CLOB(3000),
	Inhalt CLOB(3000),
	PRIMARY KEY (R_ID),
	FOREIGN KEY (Produkt_ID) REFERENCES Produkt
	ON DELETE CASCADE,
	FOREIGN KEY (Kunden_ID) REFERENCES Kunde
	ON DELETE CASCADE
	
);

create view P_K_OK as  SELECT k1.Produkt_ID, k1.Kategorie_ID, Oberkategorie.Oberkategorie_ID 
FROM Produkt_Kategorie AS k1 
Inner JOIN Oberkategorie on ( k1.Kategorie_ID = Oberkategorie.Kategorie_ID ) ;

create view Punkte as SELECT Produkt.P_ID, count(*) AS anzahlReviews, ROUND(AVG(Punkte),2) as avgPunkte 
FROM Review, Produkt 
WHERE Review.Produkt_ID = Produkt.P_ID 
group by Produkt.P_ID;

create view tops as select * 
from Produkt_Kategorie as pk left join 
Hauptkategorie as h on (pk.Kategorie_ID = h.Unterkategorie_ID) left join Punkte as p on (pk.Produkt_ID=p.P_ID) 
where p.avgPunkte is not null ;

create view a11 as (select Produkt_ID from Angebot where 
Produkt_ID not in 
(select Produkt_ID from Angebot a 
group by Produkt_ID, Filial_ID 
having (count(*)>1) ) 
group by Produkt_ID 
having (count(*)> (select count(*) from Filiale)-1) );

create view vBuch as select * from Buch;
create view vDVD as select * from DVD;
create view vMusik as select * from Musik;
create view vReview as select * from Review;

create view vDVD_Beteiligung as select * from DVD_Beteiligung;
create view vMusik_Kuenstler as select * from Musik_Kuenstler;
create view vLieder as select * from Lieder;
create view vBuch_Autor as select * from Buch_Autor;
create view vMedienperson as select * from Medienperson;
create view vKunde as select * from Kunde;
create view vAngebot as select * from Angebot;
create view vProdukt as SELECT Produkt.P_ID, Produkt.Titel, Produkt.Verkaufsrang, Produkt.Bild, Punkte.avgPunkte as Punkte FROM Produkt left join Punkte on Produkt.P_ID = Punkte.P_ID;
create view vProdukt_Kategorie as select * from Produkt_Kategorie;

create view vKategorie as select k.Kategorie_ID, k.Name, o.Oberkategorie_ID, p.Produkt_ID from Kategorie as k left join Oberkategorie as o on k.Kategorie_ID=o.Kategorie_ID left join Produkt_Kategorie as p on k.Kategorie_ID = p.Kategorie_ID;

create view vKategorie2 as SELECT k.Kategorie_ID, k.Name, k.Produkt_ID,  k.Oberkategorie_ID, o.Kategorie_ID as Unterkategorie_ID FROM vKategorie as k  left join Oberkategorie as o on o.Oberkategorie_ID=k.Kategorie_ID;



grant select on vProdukt to dbread;
grant select on vBuch to dbread;
grant select on vMusik to dbread;
grant select on vDVD to dbread;
grant select, insert on vReview to dbread;
grant select on vBuch_Autor to dbread;
grant select on vMusik_Kuenstler to dbread;
grant select on vLieder to dbread;
grant select on vDVD_Beteiligung to dbread;
grant select on vMedienperson to dbread;
grant select on vAngebot to dbread;
grant select on vKategorie to dbread;
grant select on vProdukt_Kategorie to dbread;
grant select on vKunde to dbread;



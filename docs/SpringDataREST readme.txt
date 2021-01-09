link al corso https://github.com/dlbunker/ps-guitar-rest
github https://github.com/dlbunker/ps-guitar-rest

Getting started
	nel pom.xml dobbiamo avere le dipendenze necessarie
		spring web - x integrare MVC frameword
		spring data jpa - x omtegrare JPA framework
		spring data rest
	avendo una metodo main che carica spring boot e l'app, repository RPA, model con annotations di JPA, abbiamo GRATIS i servizi REST delle nostre risorse
	x cambiare l'Uri base, impostiamo la chiave spring.data.rest.baseUri=/api
	tecnologie supportate
		Spring Data REST e' limitato solo ai repo Spring JPA!!!
Impostiamo il nostro repo RESTful
	gerarchia di interfacce: Repository<T, ID> -> CrudRepository<T, ID> -> PagingAndSortingRepository<T, ID> -> JPARepository<T, ID> -> ManufacturerJpaRepository 
							 {...		Spring Data				   ...}	   									    { Spring Data JPA }		{SQL Data Access}
	REST Controller - si avvia nel momento di startup (come fa anche Spring), scansione il codice per trovare Spring Data JPA Repository -> crea un controller MVC REST per ogni repository
	-> MVC REST Controller e' responsabile di tutti endpoint che un client puo' chiamare -> Spring DATA REST implementera' tutti i metodi del nostro repo custom -> controller cosi creato e'
	responsabile di una singola RISORSA (Leeve 2 & Level 3) HAL - ALPS, usa i verbi HTTP per aggiornamenti (UPDATE) e cancellazione (DELETE) 
	Il flusso di una richiesta: Spring MVC REST riceve una richiesta - gira la richiesta al JPA - riceve la risposta, la serializza in JSON e la restituisce al client
	NOTA: tutto questo abbiamo gratis, una volta abbiamo JPA Repository
	Spring Data REST crea un endpoint x ogni repo JPA, NON dobbiamo scrivere nessun codice.
	(aka Spring Data REST Resource Pattern)
	i verbi riconsciuti sono: GET, POST, GET, PUT, PATHC (aggiorna solo i campi passati nell'oggetto dal client), DELETE
	usando Spring Data REST abbiamo gratis operazioni GET, POST etc.. eseguibili sulla nostra risorsa.
	es. richiesta POST 
	es. richiesta PATCH che ci consente di aggiornare solo quello che ci serve (oggetto JSON contiene le props solo quelle da aggiornare)
	es. richiesta DELETE (elimina la risorla, ID passato nel PATH della risorsa)
	NOTA: al momento non abbiamo scritto nessun codice nuovo 
	come possiamo customizzare i nostri endpoint
		@RepositoryRestResource - x cambiare il path della risorsa
			es. @RepositoryRestResource(path = "mfgs", collectionResourceRel = "mfgs"), ultim parametro server per cambiare il nome della risorsa nel JSON di risposta
		@RepositoryRestResource - viene usato anche per disabilitare la risorsa di essere vista dall'esterno
Customizziamo REST payload
	NOTA: i link alle risorse generai da Spring Data REST consente navigare tutte le relazioni presenti
	Se guardiamo la chiamata a api/models, per ogni modello abbiamo i dati semplici e >1 riferimento alle proprieta' complessi (es. Produttore, Tipo Modello)
		quindi, 3 chiamate separate per ottenere tutti i dati che ci servono
	come ottenere tutti i dati in una singola chiamata?
		viene introdotto il concetto di una "Proiezzione" (Projection)
		creiamo solo le projection che ci servono 
		viene usata annotation @Projection, dove specifichiamo sia il nome che tipo del Model x quale stiamo creando la projection
		NOTA: creiamo un package per le projection, uno DIVERSO dal package di repository (altrimenti Sprting DATA REST non trovera' le proiezzioni)
		Una volta creata la projection, aggiungiamo attributi necessarie
		NOTA: tutti metodi get* nell'interfaccia della Projection devono corrispondere ai tipi e nomi presenti nel Model!!!
		acceddiamo alla projection a runtime digitando Uri es. http://host.com/api/models?projection=nomeProjection
	view/virtual projection (proiezzione virtuale)
		possiamo usare @Value con la stringa che rappresenta una espressione di Spring che determina l'output per il campo/field al quale e' stata applicata
		in questo modo possiamo predisporre una View ai nostri Client che contengono dei campi che non cambiano propri nomi nel corso di tempo ma possano cambiare il valore ritornato.
		le view assomigliano molto le view in DB, come concetto 
		pros: limitiamo i dati esposti al client (si risparmia in traffico), limitiamo modifiche impattanti client
		possiamo configurare una projection a livello del nostro repo JPA, usando @RepositoryRestResource(excerptProjection = ModelDetailView.class), in questo caso non e' necessario specificare 
			il nome della projection nella QueryString x ogni richiesta HTTP.
			Unica cosa, questa modifica ha impatto solo sui metodi diretti in repo, e NON sui singoli endpoint che ritornano una istanza etc.
			E' una decisione di Spring! Se vogliamo usare le projection sugli endpoint singoli dobbiamo sempre passare il parametro projection=myProjection.
		PROS e CONS di Projection
			CONS
				utili per le richieste di letture, richieste GET!
				non si usano per le modifiche di risorse
				client possano ignorare di usare projection nella QueryString
				NON e' una sostituzione del libello di servizi/logica di business -> dobbiamo usare qualcosa di standalone (es. JAX-RS, Spring MVC, jersey)
			PROS
				customizzazione di playload
				cambiamenti alle entita' senza impattare i client 
				molto semplice e veloce 
	NOTA: possano essere utili in CQRS , per la parte di Query 
	Jackson Payload Options:
		non fa parte di Spring, e' una libreria che possiamo integrare nella nostra app per convertire automaticamente i nostri oggetti in JSON e vice versa
		N annotazioni disponibili
		x approfondire https://github.com/FasterXML/jackson
Security
	Sicurity in REST API
		autenticazione (identificazione del client)
		autorizzazione (a cosa il clien puo' accedere)
	REST e' stateless, senza UI -> viene usato il sistema a token -> puo' essere passato nell'Url ma NON e' consigliato
	di solito il Token viene passato in HTTP Header.
	Credenziali token sono username e pwd criptati in qualche maniera -> oppure un time-based token fornito da un servizio di autenticazione -> OAuth e' l'arhitettura piu'comune 
	(OAuth e' fuori questo corso, va approfondite a parte insieme a Spring Security)
	Iter x aggiungere la sicurezza nell'app
		add dipendenza a spring security x spring boot app
		add SecurityConfiguration, qui useremo InMemoryAuthentication con autenticazione basic 
		dobbiamo fornire credenziali token in ogni chiamata a HTTP API - authorization header 
	creiamo la classe di configurazione con annotations	
		@Configuration - marca la classe come la classe di configurazione, Spring la deve caricare all'inizio di startup, e dice a spring come configurare diversi parti dell'app
		@EnableWebSecurity - abilitazione e configurazione della web security
		@EnableGlobalMethodSecurity(prePostEnabled = true) - ci consente di gestire l'autorizzazione e il check del ruoli a livello del metodo
		estendiamo la classe WebSecurityConfigurerAdapter, configurazione spring e configurazione di Spring Security, consente sovrascrivere gli hooks usati nella nostra classe di configurazione,
		per eseguire della logica di sicurezza custom
		dobbiamo impostare InMemoryAuthentication -> aggiungiamo la classe configureGlobal() che ci consente ad impostare login, pwd e ruoli degli utenti (vedi il codice)
		NOTA: NON usare questo tipo di autenticazione in PROD, dobbiamo usare OAuth
	abilitando Spring Security, automaticamente abbiamo anche CSRF  (Cross Site Request	 Forgery) -> viene usato un token salvato in un campo nascosto sulla pagina -> per avere
		CSRF disabilitato, in modo che il token viene passato in JSON, per disabilitarlo aggiungiamo il metodo void configure(HttpSecurity http), disabilitiamo con http.csrf().disable();
		in questo modo siamo in grado di chiamare le nostre API in modo sicuro
		fatto queste impostazioni, se chiamiamo endpoint senza passare le credenziali, in risposta abbiamo HTML con richiesta di inserire login e password 
		dobiamo fornirli usando autenticazione Basic. NOTA: ogni richiesta all'endpoint protetto richiede header Authorization!
	l'annotation usata prima @EnableGlobalMethodSecurity(prePostEnabled = true), ci permette di usare l'annotation @PreAuthorize("hasRole('ROLE_ADMIN')") sia sulla classe che metodo
		"hasRole('ROLE_ADMIN')" - spring security expressions
		veddi tutta la lista in doc.
		@Secured("ROLE_ADMIN) - questa annotazione accetta solo il ruolo hard coded, qui NON possiamo usare il linguaggio di espressioni Spring, o funzioni
		usiamo @PreAuthorize("hasRole('ROLE_ADMIN')") sulle classi o metodi di repo JPA per agire sui singoli metodi, classi
	Opzioni di validazione che ci sono
		usiamo JPA validation constraints (per creare constraint in base alle nostre regole di business oppure quelle a DB)
		eventi di validazione 
			BeforeCreateEvent, AfterCreateEvent (prima/dopo persistenza a db)
			BeforeSaveEvent, AfterSaveEvent
			BeforeLinkSaveEvent, AfterLinkSaveEvent
			BeforeDeleteEvent, AfterDeleteEvent
		eventi che sono eseguiti prima e/o dopo ogni singola chiamata all'API
		eventi sono usati per security authorization e auditing
	per customizzare il codice dell'errore e messaggio in una eccezzione dobbiamo creare un'altra classe di configurazione, es.
		@ControllerAdvice
		public class ControllerConfiguration {
			@ExceptionHandler(ConstraintViolationException.class)
			@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid data sent to server")
			public void notValid() {}
		}
	definizione handlers
		handler sono dei gestori scattati quando avviene un evento, per esempio nel momento di creazione, salvataggio o eliminazone di una risorsa, es.			
			@Component
			@RepositoryEventHandler(Manufacturer.class)
			public class ManufacturerEventHandler {
				@HandleBeforeCreate
				public void HandleBeforeCreate(Manufacturer manufacturer) {}
			}
		all'interno del metodo HandleBeforeCreate possiamo validare la nostra risora in qualsiasi modo che vogliamo, sollevando o no una eccezzione
		se vogliamo gestire una specifica eccezzione sollevata durante una richiesta HTTP possiamo creare dei exception hanlders relativi al tipo di eccezione e codice errore
		presente in risposta (in questo modo sovrascriviamo il msg di errore, e nel corpo del metodo possiamo eseguire altri operazioni, es. logging di errore, invio mail, etc..)
		@PreAuthorize() possiamo applicare anche nel metodo del *Handler, in questo caso, tutte le richieste (read, create, update) sono coperti dal Spring Security, tutti tranne findAll(),
			da gestire a mano.
		ci sono eventi afterCreate() etc.. event handlers ci consentono di eseguie le operazioni come validazione, check autorizzazioni, codifica/criptazione dei dati prima di salvataggio
	REST hypermedia in depth
		fornisce la descrizione di API e transazione delle chiamate
		HAL - Hypertext Application Language 
			sono i link alle risorse referenziate all'interno della risorsa richiesta
		application/hal-json: 
		HAL JSON ContentType specificato nel Header di una richiesta consente stabilire/negoziare hypermedia protocol da usare nella comunicazione con REST APIs
		HAL + ALPS (Application Level Profile Semantics), ALPS consente descrivere le risorse diponibili, loro comunicazione, check se e' un API sicura o no, 
		JSON Schema 
			e' un'altro modo x studiare e capire come usare le API
			definitisce il tipo di ogni attributo di un JSON 
		NOTA: Contenut-Type "application/hal+json", e' un formato di default di Spring Data REST
		http://localhost:8080/api/alps - fornisce elenco di tutte le risorse esposte
		http://localhost:8080/api/alps/models - dettagli della risorsa MODELS, con attributi e tipi e metodi disponibili
			utile per capire i metodi che il nostro client puo' chiamare su una risorsa
		http://localhost:8080/api/models/schema, e passando nella richiesta header Accept=application/schema+json, otteniamo una descrizione dettagliata della risorsa MODEL, attributi e tipi
			utile per costruire il proprio modello
	NOTA: Spring Data JSON genera i servizi REST sopra il layer di persistenza JPA, serializzazione automatica di oggetti in JSON, descrizione e lo schema delle risorse generate al volo,
		vale la regola "convenzione over configurazione", usando le annotation e i nomi di metodi in modo opportuno abbiamo molte funzionalita' gratis.
	
			
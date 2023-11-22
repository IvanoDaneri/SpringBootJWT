APPLICAZIONE SPRINGBOOTREST

Applicazione didattica in SpringBoot che espone http rest GET e POST con due differenti configurazioni di SpringSecurity che poi vedremo.
L'applicazione espone due controller rest che gestiscono la CRUD di compagnie ed impiegati (CompanyController, EmployeeController).
Il backend salva le entita' su un db (nel nostro caso OracleXE) per cui sono presenti gli script di creazione dello user e di popolamento iniziale
dell'anagrafica per due differenti db, test e produzione (i test junit hanno i files di properties che mappano lo user di test) 

Nell'applicazione SpringBoot:

- e' stato disabilitato il servizio Cross-Origin Resource Sharing perche' avrebbe bloccato le richieste http di tipo POST

- e' stata aggiunta la validazione del dto sulle rest di tipo POST, ossia:
	- annotazioni @NotEmpty e @NotNull sui dto CompanyDto e EmployeeDto
	- annotazione @Valid sul parametro del metodo rest
	
- sono stati definiti i ControllerAdvice che gestiscono una serie di eccezioni lanciate dai Controller rest e che verranno rilanciate al client rest (classi: ControllerNotFoundAdvice, UserControllerAdvice)


Alcuni esempi riguardo ai servizi rest:

- curl per richiedere anagrafica completa aziende: curl http://localhost:8092/springBootRest/companies
- curl per richiedere anagrafica azienda con id=1: curl http://localhost:8092/springBootRest/companies/1
- curl per richiedere anagrafica completa impiegati: curl http://localhost:8092/springBootRest/employees
- curl per richiedere anagrafica impiegato con id=2: curl http://localhost:8092/springBootRest/employees/2

Riguardo ai test di un client rest sui servizi rest esposti (gete e post):

- e' stato rilasciato l'apposito progetto SoapUI: springBootRestClient-soapui-project.xml

- sono state scritte le classi java di test sia per i service (solo per CompanyService) che per i controller (CompanyController, EmployeeController)

- sui test dei controller e' stata usata l'annotazione:
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {SpringBootApp.class})
  Questa annotazione lancia SpringBootApp compresi i servizi rest sulla porta configurata nel file di properties
  e permette quindi di invocare dalla classe di test i servizi rest usando la factory RestTemplate ed i metodi getForObject per le GET e postForObject per le POST

Vediamo la parte dell'applicazione che riguarda la sicurezza (che e' certamente la piu' interessante)
Sono state implementate due differenti configurazioni di SpringSecurity che sono alternative:

- configurazione base: qualunque utente e' autorizzato all'accesso sulle url delle rest esposte dall'applicazione (CompanyController, EmployeeController)
- configurazione JWT: effettua Autentication ed Authorization implementando il protocollo JWT (Json Web Token) che prevede il rilascio di un token di validita' (con scadenza prefissata)

L'abilitazione della configurazione e' operata dalla proprieta jwtSecurity definita nelle properties dell'applicazione (spring.security.jwt).
Vediamo la classe di configurazione dell'applicazione WebSecurityConfigurerAdapter:


@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(new AntPathRequestMatcher("/companies/**"), new AntPathRequestMatcher("/employees/**"));

    @Value("${spring.security.jwt}")
    private Boolean jwtSecurity;

    @Override
    public void configure( HttpSecurity http ) throws Exception
    {
        // JWT security enabled
        if(jwtSecurity)
        {
            http.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_READ.name())      // CompanyController GET must have AUTH_COMPANY_READ permission
                    .antMatchers(HttpMethod.POST,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_ADD.name())      // CompanyController POST must have AUTH_COMPANY_ADD permission
                    .antMatchers(HttpMethod.GET,"/employees/**").hasAuthority(PermissionEnum.AUTH_EMPLOYEE_READ.name())     // EmployeeController GET must have AUTH_EMPLOYEE_READ permission
                    .antMatchers(HttpMethod.POST,"/employees/**").hasAuthority(PermissionEnum.AUTH_EMPLOYEE_ADD.name())     // EmployeeController POST must have AUTH_EMPLOYEE_ADD permission
                    .antMatchers(HttpMethod.POST, "/logon").permitAll()                                                     // Permit logon url to everyone to pass credentials and get JWT token
                    .requestMatchers(PROTECTED_URLS)                                                                        // These are urls protected by JWTAuthorizationFilter
                    .authenticated()
                    // This configuration disable default CORS (Cross-Origin Resource Sharing) configuration
                    // that blocks rest POST calls from a client
                    .and()
                    .csrf().disable()
                    // This configuration disable all other default configurations
                    .formLogin().disable()
                    .httpBasic().disable()
                    .logout().disable();
        }
        // No security enabled for rest urls
        else
        {
            http
                    .authorizeRequests()
                    // Add url exceptions to http security configuration to avoid http authentication for rest url path
                    .antMatchers("/companies/**", "/employees/**", "/simpleEmployees/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic()
                    // This configuration disable default CORS (Cross-Origin Resource Sharing) configuration
                    // that blocks rest POST calls from a client
                    .and()
                    .csrf().disable();
        }
    }

}


La configurazione che implementa JWT prevede una rest di logon aperta che implementa la fase di Autentication:

- verifica le credenziali passate nell'header della POST di logon
- recupera i ruoli assegnati all'utente
- recupera le permission legate ai ruoli
- genera e restituisce un token di validita' (token JWT, che contiene le permissions cifrate legate al ruolo dell'utente) con una scadenza temporale prefissata (letta dal file di properties dell'applicazione)

Il token dovra' essere passato nell'header della request sulle chiamate alle rest protette (di CompanyController e EmployeeController), creando e valorizzando la proprieta' 'Authorization' con il token
restituito dalla rest di logon.

La fase di Authorization e' gestita dal filtro JWTAuthorizationFilter inserito nella filter chain che opera nel seguente modo:

- intercetta le url delle rest protette e verifica la presenza del token (proprieta 'Authorization' nell'header della request)
- verifica la validita' del token e ne estrae il Claim;
- estrae dal Claim la lista delle permission ed abilita l'accesso all'url se le permission del token comprendono le permission previste per quell'url.
  Ad esempio l'url della rest get:
       http://localhost:8092/springBootRest/companies
  prevede la permission: 
       PermissionEnum.AUTH_COMPANY_READ 
  come configurato in WebSecurityConfigurerAdapter:
       .antMatchers(HttpMethod.GET,"/companies/**").hasAuthority(PermissionEnum.AUTH_COMPANY_READ.name())
  quindi tra le permission estratte dal token deve essere presente PermissionEnum.AUTH_COMPANY_READ, in caso contrario il filtro restituira: 404 Access Forbidden
  
Come si diceva, la gestione di autenticazione/autorizzazione impiega il protocollo standard JSON Web Token (JWT, see https://jwt.io/).
Il processo di autenticazione/autorizzazione e' descritto nell'articolo su Softtek:
Token-based API authentication with Spring and JWT
L'articolo prevede:
- un authentication server che verifica le credenziali ed autentica il client rilasciando un token di validita' con una scadenza temporale prefissata (token JWT)
- un authorization server che per ogni richiesta protetta accetta il token e conferma se il client ha i permessi per accedere a quella risorsa



ALTRE APPLICAZIONI

GsmcHsmAuto: applicazione desktop per lanciare le istanze dell'hsm ed impostare lo stato operational

hsmRemoteControl: applicazione in SpringBoot che espone http rest GET per lanciare le istanze dell'hsm ed impostare lo stato operational (1)

Nota: e' rimasto aperto il problema di caricare in Sikuli i file .PNG che servono per individurae le immagini sullo schermo

curl "http://192.168.255.11:8092/hsmRemoteControl/startHsm"
curl "http://192.168.255.11:8092/hsmRemoteControl/setHsmOperational"
curl "http://192.168.255.11:8092/hsmRemoteControl/stoptHsm"

Nuova macchina virtuale:

Nome macchina virtuale: test_dev-vm
Hostname: test-vm01 (visibile dalla mia postazione)
IP: 172.16.0.10 (visibile dalla mia postazione)
Credenziali: Administrator/1q2w3e4r..

Per condividere dati c'e' il folder tmp condiviso, ossia basta fare dalla mia postazione su explorer:

\\test-vm01\tmp

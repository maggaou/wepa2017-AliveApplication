package wad.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wad.domain.FileObject;
import wad.domain.Kategoria;
import wad.domain.Kirjoittaja;
import wad.domain.Lukukerta;
import wad.domain.Uutinen;
import wad.repository.FileObjectRepository;
import wad.repository.KategoriaRepository;
import wad.repository.KirjoittajaRepository;
import wad.repository.LukukertaRepository;
import wad.repository.UutinenRepository;

@Controller
public class UutisetController {

    @Autowired
    UutinenRepository uutinenRepository;

    @Autowired
    LukukertaRepository lukukertaRepository;

    @Autowired
    FileObjectRepository fileObjectRepository;

    @Autowired
    KategoriaRepository kategoriaRepository;

    @Autowired
    KirjoittajaRepository kirjoittajaRepository;

    @GetMapping("/lahetauutinen")
    public String naytaUutisenLahetys() {
        return "uutisen_lisaaminen";
    }

    @PostMapping("/lahetauutinen")
    public String lahetaUutinen(@RequestParam Map parametrit, @RequestParam("kuva") MultipartFile kuva) throws Exception {
        MultipartFile inputTiedosto = kuva;
        FileObject tiedosto = new FileObject();
        tiedosto.setNimi(inputTiedosto.getOriginalFilename());
        tiedosto.setMediatyyppi(inputTiedosto.getContentType());
        tiedosto.setKoko(inputTiedosto.getSize());
        tiedosto.setSisalto(inputTiedosto.getBytes());
        FileObject uutisKuva = fileObjectRepository.save(tiedosto);
        Uutinen uutinen = uutinenRepository.save(new Uutinen());
        uutinen.setKuva(uutisKuva);
        uutinen.setOtsikko((String) parametrit.get("otsikko"));
        uutinen.setIngressi((String) parametrit.get("ingressi"));
        uutinen.setSisalto((String) parametrit.get("sisalto"));
        uutinen.setJulkaisuaika(new Date());
        List<Kirjoittaja> kirjoittajat = kasitteleKirjoittajat((String) parametrit.get("kirjoittajat"), uutinen);
        List<Kategoria> kategoriat = kasitteleKategoriat((String) parametrit.get("kategoriat"), uutinen);
        uutinen.setKirjoittajat(kirjoittajat);
        uutinen.setKategoriat(kategoriat);
        uutinen = uutinenRepository.save(uutinen);
        //return "uutisen_lisaaminen";
        return "redirect:/uutinen/" + uutinen.getId();
    }

    public List<Kirjoittaja> kasitteleKirjoittajat(String input, Uutinen uutinen) {
        if (input.isEmpty()) {
            return null;
        }
        List<Kirjoittaja> kirjoittajat = new ArrayList();
        String[] palat = input.split(",");
        for (String nimi : palat) {
            String s = nimi.trim();
            Kirjoittaja kirjoittaja = kirjoittajaRepository.findByNimi(s);
            if (kirjoittaja == null) {
                kirjoittaja = new Kirjoittaja(s);
                kirjoittaja = kirjoittajaRepository.save(kirjoittaja);
            }
            List<Uutinen> kirjoittajanUutiset = kirjoittaja.getUutiset();
            if (!kirjoittajanUutiset.contains(uutinen)) {
                kirjoittajanUutiset.add(uutinen);
            }
            kirjoittaja.setUutiset(kirjoittajanUutiset);
            kirjoittajaRepository.save(kirjoittaja);
            kirjoittajat.add(kirjoittaja);
        }
        return kirjoittajat;
    }

    public List<Kategoria> kasitteleKategoriat(String input, Uutinen uutinen) {
        if (input.isEmpty()) {
            return null;
        }
        List<Kategoria> kategoriat = new ArrayList();
        String[] palat = input.split(",");
        for (String nimi : palat) {
            String s = nimi.trim().toLowerCase();
            Kategoria kategoria = kategoriaRepository.findByNimi(s);
            if (kategoria == null) {
                kategoria = new Kategoria(s);
                kategoria = kategoriaRepository.save(kategoria);
            }
            List<Uutinen> kategorianUuutiset = kategoria.getUutiset();
            if (!kategorianUuutiset.contains(uutinen)) {
                kategorianUuutiset.add(uutinen);
            }
            kategoria.setUutiset(kategorianUuutiset);
            kategoriaRepository.save(kategoria);
            kategoriat.add(kategoria);
        }
        return kategoriat;
    }

    @GetMapping("/")
    public String listaaUutisetEtusivu(Model model) {
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.Direction.DESC, "julkaisuaika");
        Iterable<Uutinen> uutiset = uutinenRepository.findAll(pageRequest);
        model.addAttribute("uutiset", uutiset);
        model.addAttribute("otsikko", "Wepa2017 uutissivusto");
        model.addAttribute("paivamaarat", julkaisupaivamaarat(uutiset, "H:mm"));
        return "uutislistaus.html";
    }

    @GetMapping("/uutinen/{id}")
    public String naytaUutinen(Model model, @PathVariable long id, @RequestParam Map<Object, Object> param, HttpSession session) {
        Sort uusimmastaVanmpimpaan = new Sort(Sort.Direction.DESC, "julkaisuaika");
        Sort luetuimmastaVahitenluettuun = new Sort(Sort.Direction.DESC, "lukumaara");
        List<Uutinen> uusimmat = uutinenRepository.findAll(uusimmastaVanmpimpaan);
        List<Uutinen> luetuimmat = uutinenRepository.findAll(luetuimmastaVahitenluettuun);
        model.addAttribute("uusimmat", uusimmat);
        model.addAttribute("paivamaaratUusimmat", julkaisupaivamaarat(uusimmat, "H:mm"));
        model.addAttribute("luetuimmat", luetuimmat);
        model.addAttribute("paivamaaratLuetuimmat", julkaisupaivamaarat(luetuimmat, "d.M.YYYY, H:mm"));

        Uutinen uutinen = uutinenRepository.findById(id).get();
        int luettuNyt = uutinen.getLukumaara();
        uutinen.setLukumaara(luettuNyt + 1);
        uutinenRepository.save(uutinen);

        model.addAttribute("uutinen", uutinen);
        model.addAttribute("viesti", param.get("viesti"));
        Object muokkaus = session.getAttribute("muokkaus");
        model.addAttribute("muokkaus", muokkaus);
        if (uutinen.isPaivitetty()) {
            model.addAttribute("paivitetty", muokkauspaiva(uutinen, "d.M.YYYY, H:mm"));
        }
        model.addAttribute("julkaistu", julkaisupaiva(uutinen, "d.M.YYYY, H:mm"));
        Lukukerta lukukerta = new Lukukerta();
        lukukerta.setAjankohta(new Date());
        lukukerta.setUutinen(uutinen);
        lukukertaRepository.save(lukukerta);
        System.out.println("lukukerta tallennettiin!");
        model.addAttribute("kategoriat", kategoriaRepository.findAll());
        return "uutissivu.html";
    }

    @GetMapping("/uutiset/kategoria/{nimi}")
    public String listaaKategorianUutiset(Model model, @PathVariable String nimi) {
        Kategoria kategoria = kategoriaRepository.findByNimi(nimi);
        List<Uutinen> uutiset = kategoria.getUutiset();
        model.addAttribute("uutiset", uutiset);
        model.addAttribute("paivamaarat", julkaisupaivamaarat(uutiset, "d.M.YYYY, H:mm"));
        model.addAttribute("otsikko", "Kategorian " + kategoria.getNimi() + " uutiset");
        List<Kategoria> kategoriat = kategoriaRepository.findAll();
        System.out.println("Mitä kategorioita löydettiin?");
        for (Kategoria kategoria1 : kategoriat) {
            System.out.println("'" + kategoria1.getNimi() + "', onko navigaatiovalikossa:" + kategoria1.getNavigaatioValikossa());
        }
        model.addAttribute("kategoriat", kategoriat);
        return "uutislistaus_1.html";
    }

    @GetMapping("/uutiset")
    public String listaaUutisetParametrilla(Model model, @RequestParam String jarjestys) {
        System.out.println("vastaanotettiin parametri " + jarjestys);
        List<Uutinen> uutiset = new ArrayList();
        Sort jarjestysTapa = null;
        // julkaisuajan perusteella
        if (jarjestys.equals("julkaisuaika")) {
            jarjestysTapa = new Sort(Sort.Direction.DESC, "julkaisuaika");
            uutiset = uutinenRepository.findAll(jarjestysTapa);
            model.addAttribute("otsikko", "Tuoreimmat uutiset");
            model.addAttribute("paivamaarat", julkaisupaivamaarat(uutiset, "H:mm"));
        }
        // edellisen viikon lukukertojen perusteella..
        if (jarjestys.equals("luetuimmatEdelViikko")) {
            List<Lukukerta> kaikkiLukukerrat = lukukertaRepository.findAll();
            System.out.println("lukukertoja oli " + kaikkiLukukerrat.size());
            Date viikkoSitten = new Date(System.currentTimeMillis() - new Long("604800000"));
            for (Lukukerta lukukerta : kaikkiLukukerrat) {
                if (lukukerta.getAjankohta().getTime() > viikkoSitten.getTime()) {
                    if (!uutiset.contains(lukukerta.getUutinen())) {
                        uutiset.add(lukukerta.getUutinen());
                    }
                }
            }
            Collections.sort(uutiset);
            SimpleDateFormat pvmGenerointi = new SimpleDateFormat("d.M.YYYY");
            model.addAttribute("otsikko", "Luetuimmat uutiset "
                    + pvmGenerointi.format(viikkoSitten) + " - " + pvmGenerointi.format(new Date()));
            model.addAttribute("paivamaarat", julkaisupaivamaarat(uutiset, "d.M.YYYY"));
        }
        model.addAttribute("uutiset", uutiset);
        return "uutislistaus_1.html";
    }

    @PostMapping("/muokkaa/{id}")
    public String muokkaaUutista(@PathVariable Long id, @RequestParam Map<String, Object> param, RedirectAttributes attr) {
        Uutinen uutinen = uutinenRepository.findById(id).get();
        //poistetaan ensin kaikilta kirjoittajilta tämä uutinen!
        List<Kirjoittaja> tamanUutisenKirjoittajat = uutinen.getKirjoittajat();
        for (Kirjoittaja kirjoittaja : tamanUutisenKirjoittajat) {
            List<Uutinen> kirjoittajanUuutiset = kirjoittaja.getUutiset();
            kirjoittajanUuutiset.remove(uutinen);
        }
        //sama kategorioille
        List<Kategoria> tamanUutisenKategoriat = uutinen.getKategoriat();
        for (Kategoria kategoria : tamanUutisenKategoriat) {
            List<Uutinen> kategorianUutiset = kategoria.getUutiset();
            kategorianUutiset.remove(uutinen);
        }
        uutinen.setOtsikko((String) param.get("otsikko"));
        uutinen.setIngressi((String) param.get("ingressi"));
        uutinen.setSisalto((String) param.get("sisalto"));
        uutinen.setPaivitetty(true);
        uutinen.setPaivitysaika(new Date());
        String kirjoittajat = (String) param.get("kirjoittajat");
        uutinen.setKirjoittajat(kasitteleKirjoittajat(kirjoittajat, uutinen));
        String kategoriat = (String) param.get("kategoriat");
        uutinen.setKategoriat(kasitteleKategoriat(kategoriat, uutinen));
        uutinenRepository.save(uutinen);
        attr.addAttribute("viesti", "Muokkaaminen onnistui!");
        return "redirect:/uutinen/" + id;
    }

    @GetMapping("/muokkaa/{id}")
    public String naytaUutisenMuokkaus(@PathVariable Long id, Model model) {
        Uutinen uutinen = uutinenRepository.findById(id).get();
        String kirjoittajat = "";
        for (Kirjoittaja kirjoittaja : uutinen.getKirjoittajat()) {
            kirjoittajat += ", " + kirjoittaja.getNimi();
        }
        kirjoittajat = kirjoittajat.replaceFirst(", ", "");
        String kategoriat = "";
        for (Kategoria kategoria : uutinen.getKategoriat()) {
            kategoriat += ", " + kategoria.getNimi();
        }
        kategoriat = kategoriat.replaceFirst(", ", "");
        model.addAttribute("uutinen", uutinen);
        model.addAttribute("kategoriat", kategoriat);
        model.addAttribute("kirjoittajat", kirjoittajat);
        return "uutisen_muokkaus.html";
    }

    @GetMapping("/muokkaus")
    public String muokkaustila(HttpSession session) {
        Object muokkaus = session.getAttribute("muokkaus");
        if (muokkaus == null) {
            session.setAttribute("muokkaus", true);
            return "redirect:/";
        }
        if ((boolean) session.getAttribute("muokkaus")) {
            session.setAttribute("muokkaus", false);
        } else {
            session.setAttribute("muokkaus", true);
        }
        return "redirect:/";
    }

    @GetMapping("/hallinta")
    public String naytaUutistenHallinta(@RequestParam Map<String, Object> param, Model model) {
        model.addAllAttributes(param);
        model.addAttribute("kategoriat", kategoriaRepository.findAll());
        return "hallintapaneeli.html";
    }

    private static Map<Long, String> julkaisupaivamaarat(Iterable<Uutinen> uutiset, String muoto) {
        Map<Long, String> paivamaarat = new HashMap();
        DateFormat pvmGenerointi = new SimpleDateFormat(muoto);
        for (Uutinen uutinen : uutiset) {
            Date pvm = uutinen.getJulkaisuaika();
            paivamaarat.put(uutinen.getId(), pvmGenerointi.format(pvm));
        }
        return paivamaarat;
    }

    private static Map<Long, String> muokkauspaivamaarat(Iterable<Uutinen> uutiset, String muoto) {
        Map<Long, String> paivamaarat = new HashMap();
        DateFormat pvmGenerointi = new SimpleDateFormat(muoto);
        for (Uutinen uutinen : uutiset) {
            Date pvm = uutinen.getPaivitysaika();
            paivamaarat.put(uutinen.getId(), pvmGenerointi.format(pvm));
        }
        return paivamaarat;
    }

    private static String muokkauspaiva(Uutinen uutinen, String muoto) {
        Map<Long, String> paivamaarat = new HashMap();
        DateFormat pvmGenerointi = new SimpleDateFormat(muoto);
        Date pvm = uutinen.getPaivitysaika();
        return pvmGenerointi.format(pvm);
    }

    private static String julkaisupaiva(Uutinen uutinen, String muoto) {
        Map<Long, String> paivamaarat = new HashMap();
        DateFormat pvmGenerointi = new SimpleDateFormat(muoto);
        Date pvm = uutinen.getJulkaisuaika();
        return pvmGenerointi.format(pvm);
    }

    @PostMapping("/valitsekategoriat")
    public String valitseKategoriat(@RequestParam Map<String, String> kategoriat, RedirectAttributes attr) {
        // poistetaan ensin nykyiset kategoriat navigaatiosta
        List<Kategoria> kaikkiKategoriat = kategoriaRepository.findAll();
        for (Kategoria kategoria : kaikkiKategoriat) {
            kategoria.setNavigaatioValikossa(false);
        }
        System.out.println("valitaan nämä kategoriat: " + kategoriat.keySet());
        List<Kategoria> valittavat = new ArrayList();
        for (String kategoria : kategoriat.keySet()) {
            Kategoria loyd = kategoriaRepository.findByNimi(kategoria);
            valittavat.add(loyd);
            System.out.println("Löydettiin kategoria: " + loyd.getNimi());
        }
        for (Kategoria kategoria : valittavat) {
            kategoria.setNavigaatioValikossa(true);
            Kategoria tal = kategoriaRepository.save(kategoria);
            System.out.println("Tallennettiin " + tal.getNimi() + ": " + tal.getId() + ", näytetään valikossa: " + tal.getNavigaatioValikossa());
        }
        attr.addAttribute("viesti", "Kategorioiden päivitys onnistui");
        return "redirect:/hallinta";
    }

    @GetMapping("/valitsekategoriat")
    public String naytaValitseKategoriat(Model model) {
        List<Kategoria> kategoriat = kategoriaRepository.findAll();
        model.addAttribute("kategoriat", kategoriat);
        System.out.println("kategoriat: " + kategoriat);
        model.addAttribute("kategoriatTyhja", kategoriat.isEmpty());

        return "valitsekategoriat.html";
    }
}

import java.util.ArrayList;
import java.util.Calendar;
import model.*;
public class Programma {
    public static void main(String[] args) {


        //creazione attività
        Attività a1 = new Attività("comprare il latte", false);
        Attività a2 = new Attività("comprare il pane", false);
        Attività a3 = new Attività("comprare le uova", false);



        //creazione checklist
        ArrayList<Attività> SerieAttività = new ArrayList<>();
        SerieAttività.add(a1);
        SerieAttività.add(a2);
        SerieAttività.add(a3);
        Checklist c1 = new Checklist(SerieAttività);

        //creazione ToDo
        Calendar scadenza = Calendar.getInstance();
        scadenza.set(2025, 10, 15);

        //usando costruttore completo
        ToDo t1 = new ToDo("comprare la spesa", "comprare la spesa al supermercato", "www.supermercato.it", scadenza, false, false, false, "sfondo", "immagine", c1);
        //usando cotruttore senza elementi null
        ToDo t2 = new ToDo("cucinare", "cucinare le polpette",  scadenza, false, false, false);





        //creazione bacheca
        ArrayList<ToDo> listaToDo1 = new ArrayList<>();
        listaToDo1.add(t1);
        listaToDo1.add(t2);
        Bacheca b1 = new Bacheca(Titolo.TempoLibero, "Bacheca per la spesa", Ordinamento.AZ, listaToDo1);

        //creazione utente
        Bacheca bacheche[]= new Bacheca[3];
        bacheche[0] = b1;
        Utente u1= new Utente("giovanni@gmail.com", "3457890", bacheche);

        //stampa prima attività partendo da utente
        //in modo tale da risalire tutte le classi
        System.out.println("Prima attività: " + u1.getBacheche()[0].getToDoList().get(0).getChecklist().getAttività().get(0).getNome());


        
        //creazione seconda bacheca per condivisione
        Bacheca bacheche2[]= new Bacheca[3];
        ArrayList<ToDo> listaToDo2 = new ArrayList<>();

        //creazione todo per condivisione usando il costruttore senza elementi null
        ToDo t3 = new ToDo("comprare la frutta", "comprare la frutta al mercato", scadenza, false, false, false);
        listaToDo2.add(t3);
        Bacheca b2 = new Bacheca(Titolo.TempoLibero, "Bacheca prova condivisione", Ordinamento.AZ, listaToDo2);
        bacheche2[0] = b2;

        //creazione utente per condivisione
        Utente u2 = new Utente("storti@gmail.com", "356780", bacheche2);


        //creazione condivisione
        ArrayList<Utente> condivisiCon = new ArrayList<>();
        condivisiCon.add(u2);
        Condivisione co1 = new Condivisione(u1, t1,condivisiCon);

        //stampa condivisione
        System.out.println("Condivisione: " + co1.getToDoCondiviso().getTitolo() + " condiviso con: " + co1.getCondivisiCon().get(0).getEmail());
    }
}

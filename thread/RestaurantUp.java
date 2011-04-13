package threads;

/* L�sningsforslag oppgave 2 og 3, kapittel 17.
 * Lagres p� fil Restaurant.java
 * 
 * Feilen oppst�r i den ene kokken har sjekket at fler tallerkner skal
 * leveres og g�r inn i while l�kken. F�r kallet p� rest.putTallerken
 * er det ingen l�s, og den andre kokken rekker � komme inn i sin
 * while-l�kke. Dermed vil begge kokkene befinne seg i while-l�kka og
 * det blir produsert minst en tallerken for mye. Det hjelper med
 * andre ord ikke at kokkFerdig-metoden er synkronisert, s� lenge
 * kokken slipper l�sen f�r kallet p� putTallerken-metoden.
 *
 * En mulig l�sning er � legge kallet p� kokkFerdig-metoden i
 * putTallerken-metoden. Hvis vi i tillegg lar putTallerken-metoden
 * returnere svaret fra kokkFerdig-metoden kan vi bruke denne som
 * betingelsen i while-l�kka.
 */


public class RestaurantUp {
    int antBestilt;
    int antLaget = 0, antServert = 0; // tallerkenrertter
    int antKokker = 6, antServitorer = 1;

    RestaurantUp(int ant) {
        antBestilt = ant;
        for (int i = 0; i < antKokker; i++) {
            KokkUp k = new KokkUp(this, "Kokk nr. " + (i + 1));
            k.start();
        }

        for (int i = 0; i < antServitorer; i++) {
            ServitorUp s = new ServitorUp(this, "Servit�r nr. " + (i + 1));
            s.start();
        }

    }

    public static void main(String[] args) {
        if (args.length > 0)
            new RestaurantUp(Integer.parseInt(args[0]));
        else
            new RestaurantUp(9);
    }

    synchronized boolean kokkFerdig() {
        return antLaget == antBestilt;
    }

    synchronized boolean servitorFerdig() {
        return antServert == antBestilt;
    }

    synchronized boolean putTallerken(KokkUp k) {
        // Kokketr�den blir eier av l�sen.

        while (antLaget - antServert > 2) {
            /* s� lenge det er minst 2 tallerkner
                * som ikke er servert, skal kokken vente. */
            try {
                System.out.println(Thread.currentThread().getName() + " legger seg til � sove.");
                wait(); /* Kokketr�den gir fra seg
				 * l�sen og sover til den 
				 * blir vekket */
                System.out.println(Thread.currentThread().getName() + " v�kner opp.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Kokketr�den blir igjen eier av l�sen

        }

        boolean ferdig = kokkFerdig();
        if (!ferdig) {
            antLaget++;
            System.out.println(k.getName() + " laget nr: " + antLaget);
        }

        notify(); /* Si ifra til servit�ren. */

        return !ferdig;
    }

    synchronized boolean getTallerken(ServitorUp s) {
        // Servit�rtr�den blir eier av l�sen.

        while (antLaget == antServert && !servitorFerdig()) {
            /* s� lenge kokken ikke har plassert
                * en ny tallerken. Dermed skal
                * servit�ren vente. */
            try {
                System.out.println(Thread.currentThread().getName() + " legger seg til � sove.");
                wait(); /* Servit�rtr�den gir fra seg
				 * l�sen og sover til den
				 * blir vekket */
                System.out.println(Thread.currentThread().getName() + " v�kner opp.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Servit�rtr�den blir igjen eier av l�sen.
        }

        boolean ferdig = servitorFerdig();
        if (!ferdig) {
            antServert++;
            System.out.println(s.getName() + " serverer nr: " + antServert);
        }


        notify(); /* si ifra til kokken */
        return !ferdig;
    }
}

class KokkUp extends Thread {
    RestaurantUp rest;

    KokkUp(RestaurantUp rest, String navn) {
        super(navn); // Denne tr�den heter n�
        this.rest = rest;
    }

    public void run() {
        while (rest.putTallerken(this)) {
            // levert tallerken.

            /*try {
                   sleep((long) (1000 * Math.random()));
               } catch (InterruptedException e) {}*/
        }
        // Kokken er ferdig
    }
}

class ServitorUp extends Thread {
    RestaurantUp rest;

    ServitorUp(RestaurantUp rest, String navn) {
        super(navn); // Denne tr�den heter n�
        this.rest = rest;
    }

    public void run() {
        while (rest.getTallerken(this)) {
            /*try {
                   sleep((long) (1000 * Math.random()));
               } catch (InterruptedException e) {}*/
        }
        // Servit�ren er ferdig

    }
}




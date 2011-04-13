package threads;

public class RestaurantJP {
    int antBestilt;
    int antLaget = 0, antServert = 0; // tallerkenretter

    RestaurantJP(int ant) {
        antBestilt = ant;
    }

    public static void main(String[] args) {
        /**
         * Sett antall tallerkner, kokker og servit�rer
         **/
        int n = 9;
        int antallKokker = 6;
        int antallServitorer = 1;
        /***/
        Thread[] t = new Thread[antallKokker + antallServitorer];
        RestaurantJP rest = new RestaurantJP(n);

        for (int i = 1; i <= antallKokker; i++) {
            t[i - 1] = new KokkJP(rest, "Kokk " + i);
            t[i - 1].start();
        }
        /*try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }*/
        for (int i = 1; i <= antallServitorer; i++) {
            t[antallKokker + i - 1] = new ServitorJP(rest, "Servt�r " + i);
            t[antallKokker + i - 1].start();
        }

        for (Thread tt : t) {
            try {
                tt.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Alle tr�der er avsluttet");
    }

    synchronized boolean kokkFerdig() {
        return antLaget == antBestilt;
    }

    synchronized boolean servitorFerdig() {
        return antServert == antBestilt;
    }

    synchronized void putTallerken() {
        // Kokketr�den blir eier av l�sen.

        //System.out.println(antLaget);

        while (antLaget - antServert >= 2) {
            /* s� lenge det er minst 2 tallerkner
                * som ikke er servert, skal kokken vente. */
            try {
                System.out.println(Thread.currentThread().getName() + " la seg til � sove");
                wait(); /* Kokketr�den gir fra seg */
                System.out.println(Thread.currentThread().getName() + " v�kner opp");
                /*l�sen og sover til den
                 * blir vekket */
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
            // Kokketr�den blir igjen eier av l�sen.
        }

        //if (kokkFerdig()) return; // fiks

        if (!kokkFerdig()) {
            antLaget++;
            System.out.println(Thread.currentThread().getName()
                    + " laget nr " + antLaget);
        }
        notify(); /* Si ifra til servit{�}ren. */
    }

    synchronized void getTallerken() {
        // Servit�rtr�den blir eier av l�sen.


        while (antLaget == antServert) {
            /* s� lenge kokken ikke har plassert
                * en ny tallerken. Dermed skal
                * servit�ren vente. */
            try {
                wait(); /* Servit�rtr�den gir fra seg
			 * l�sen og sover til den
			 * blir vekket */
            } catch (InterruptedException e) {
            }
            // Servit�rtr�den blir igjen eier av l�sen.
        }

        //if (servitorFerdig()) return; // fiks

        if (!servitorFerdig()) {
            antServert++;
            System.out.println(Thread.currentThread().getName()
                    + " serverer nr " + antServert);
        }
        notify(); /* si ifra til kokken. */
    }
} // end class Restaurant0


class KokkJP extends Thread {
    RestaurantJP rest;

    KokkJP(RestaurantJP rest, String name) {
        super(name);
        this.rest = rest;
    }

    public void run() {
        while (!rest.kokkFerdig()) {
            rest.putTallerken();  // lever tallerken
            /*try {
                sleep((long) (1000 * Math.random()));
            } catch (InterruptedException e) {
            } */
        }
        // Kokken er ferdig
    }
}

class ServitorJP extends Thread {
    RestaurantJP rest;

    ServitorJP(RestaurantJP rest, String name) {
        super(name);
        this.rest = rest;
    }

    public void run() {
        while (!rest.servitorFerdig()) {
            rest.getTallerken(); /* hent tallerken og
			 * server */
            /*try {
                sleep((long) (1000 * Math.random()));
            } catch (InterruptedException e) {
            } */
        }
        // Servit�ren er ferdig
    }
}


package threads;

public class Restaurant {
    int antBestilt;
    int antLaget = 0, antServert = 0; // tallerkenretter

    Restaurant(int ant) {
        antBestilt = ant;
    }

    public static void main(String[] args) {
        /***/
        int n = 9;
        /***/
        Restaurant rest = new Restaurant(n);
        Kokk kokk = new Kokk(rest);
        kokk.start();
        Kokk kokk2 = new Kokk(rest);
        kokk2.start();
        Servitor servitor = new Servitor(rest);
        servitor.start();
    }

    synchronized boolean kokkFerdig() {
        return antLaget == antBestilt;
    }

    synchronized boolean servitorFerdig() {
        return antServert == antBestilt;
    }

    synchronized void putTallerken() {
        // Kokketr�den blir eier av l�sen.

        while (antLaget - antServert > 2) {
            /* s� lenge det er minst 2 tallerkner
                * som ikke er servert, skal kokken vente. */
            try {
                wait(); /* Kokketr�den gir fra seg
			 * l�sen og sover til den
			 * blir vekket */
            } catch (InterruptedException e) {
            }
            // Kokketr�den blir igjen eier av l�sen.
        }
        antLaget++;
        System.out.println("Kokken laget nr " + antLaget);

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
        antServert++;
        System.out.println("Servit�r serverer nr:" +
                antServert);
        notify(); /* si ifra til kokken. */
    }
} // end class Restaurant0


class Kokk extends Thread {
    Restaurant rest;

    Kokk(Restaurant rest) {
        this.rest = rest;
    }

    public void run() {
        while (!rest.kokkFerdig()) {
            rest.putTallerken();  // lever tallerken
            try {
                sleep((long) (1000 * Math.random()));
            } catch (InterruptedException e) {
            }
        }
        // Kokken er ferdig
    }
}

class Servitor extends Thread {
    Restaurant rest;

    Servitor(Restaurant rest) {
        this.rest = rest;
    }

    public void run() {
        while (!rest.servitorFerdig()) {
            rest.getTallerken(); /* hent tallerken og
			 * server */
            try {
                sleep((long) (1000 * Math.random()));
            } catch (InterruptedException e) {
            }
        }
        // Servit�ren er ferdig
    }
}


package cc.controlReciclado;

import es.upm.babel.cclib.Semaphore;

import java.util.Random;

public class ApiGruas {
  public final int MAX_GRUAS;

  public final int MIN_P_GRUA;

  public final int MAX_P_GRUA;

  private final int TIEMPO_MIN_SOLTAR_MS;

  private final int TIEMPO_MAX_SOLTAR_MS;

  private Semaphore mutex = new Semaphore(1);

  private Random random = new Random(0);

  private ApiContenedor contenedor;

  private int[] cargas;

  public ApiGruas(ApiContenedor contenedor,
                  int max_gruas,
                  int min_p_grua,
                  int max_p_grua) {
    this.contenedor = contenedor;
    MAX_GRUAS = max_gruas;
    MIN_P_GRUA = min_p_grua;
    MAX_P_GRUA = max_p_grua;
    TIEMPO_MIN_SOLTAR_MS = MIN_P_GRUA / 10;
    TIEMPO_MAX_SOLTAR_MS = MAX_P_GRUA / 10;
    cargas = new int[MAX_GRUAS];
    for (int idGrua = 0; idGrua < MAX_GRUAS; idGrua++) {
      cargas[idGrua] = 0;
    }
  }

  public int recoger(int idGrua) {
    mutex.await();
    int carga = MIN_P_GRUA + random.nextInt(MAX_P_GRUA - MIN_P_GRUA);
    cargas[idGrua] = carga;
    mutex.signal();
    try {
      Thread.sleep(2 * carga);
    } catch (InterruptedException x) {}
    return carga;
  }

  public void soltar(int idGrua) {
    try {
      mutex.await();
      int t = random.nextInt(TIEMPO_MAX_SOLTAR_MS - TIEMPO_MIN_SOLTAR_MS);
      contenedor.incrementar(cargas[idGrua]);
      cargas[idGrua] = 0;
      mutex.signal();
      Thread.sleep(TIEMPO_MIN_SOLTAR_MS + t);
    } catch (InterruptedException x) {}
  }
}

<template>
  <div>

    <div>
      <label for="sihtkoht">Vali sihtkoht:</label>
      <select id="sihtkoht" v-model="filtrid.sihtkoht">
        <option value="">Kõik sihtkohad</option>
        <option v-for="sihtkoht in sihtkohad" :key="sihtkoht" :value="sihtkoht">
          {{ sihtkoht }}
        </option>
      </select>

      <label for="date">Vali kuupäev:</label>
      <input id="date" v-model="filtrid.kuupäev" type="date" />

      <label for="kestvus">Lennu pikkus (kuni {{ filtrid.kestvus }} tundi):</label>
      <input
          id="kestvus"
          v-model.number="filtrid.kestvus"
          type="range"
          min="1"
          max="5"
          step="1"
      />
      <span>{{ filtrid.kestvus }} tundi</span>
    </div>


    <ul>
      <li
          v-for="lend in filtreeritudLennud"
          :key="lend.id"
          @click="valiLend(lend)"
          style="cursor: pointer; background: #f5f5f5; padding: 10px; margin: 5px; border-radius: 5px;"
      >
        <strong>{{ lend.sihtkoht }}</strong> - {{ lend.kuupäev }}
        Algus {{ lend.lennualgusaeg }} | Lõpp {{ lend.lennulõppaeg }}
        - Hind €{{ lend.hind }} | Lennuk {{ lend.lennuk }}
        - Kestvus {{ arvutaKestvus(lend.lennualgusaeg, lend.lennulõppaeg) }} tundi
      </li>
    </ul>


    <p v-if="valitudLend">
      Valisid lennu sihtkohaga <strong>{{ valitudLend.sihtkoht }}</strong>
    </p>


    <div v-if="valitudLend && istmed.length > 0">
      <h3>Istmed lennuki: {{ valitudLend.lennuk }}</h3>
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>Jalaruum</th>
          <th>Aken</th>
          <th>Exit</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="iste in istmed" :key="iste.id">
          <td>{{ iste.id }}</td>
          <td>{{ iste.jalaruum ? '+' : '-' }}</td>
          <td>{{ iste.aken ? '+' : '-' }}</td>
          <td>{{ iste.exit ? '+' : '-' }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { getFlights, getSeatsByLennuk } from "@/api";


const lennud = ref([]);
const valitudLend = ref(null);
const istmed = ref([]);


const filtrid = ref({
  sihtkoht: '',
  kuupäev: '',
  kestvus: 5,
});


onMounted(async () => {
  try {
    const data = await getFlights();
    lennud.value = data;
  } catch (error) {
    console.error("Viga lendude laadimisel:", error);
  }
});


const sihtkohad = computed(() => {
  const kohad = lennud.value.map(lend => lend.sihtkoht);
  return [...new Set(kohad)];
});

//Chatgpt - arvutaKestvus
const arvutaKestvus = (startTime, endTime) => {
  if (!startTime || !endTime) return 0;
  const [startHours, startMinutes] = startTime.split(":").map(Number);
  const [endHours, endMinutes] = endTime.split(":").map(Number);
  const kestvus = (endHours * 60 + endMinutes - (startHours * 60 + startMinutes)) / 60;
  return Math.max(kestvus, 0);
};


const filtreeritudLennud = computed(() => {
  return lennud.value.filter(lend => {
    const lennuKestvus = arvutaKestvus(lend.lennualgusaeg, lend.lennulõppaeg);
    return (
        (!filtrid.value.sihtkoht || lend.sihtkoht === filtrid.value.sihtkoht) &&
        (!filtrid.value.kuupäev || lend.kuupäev === filtrid.value.kuupäev) &&
        (!filtrid.value.kestvus || lennuKestvus <= filtrid.value.kestvus)
    );
  });
});


const valiLend = async (lend) => {
  valitudLend.value = lend;
  istmed.value = await getSeatsByLennuk(lend.lennuk);
};
</script>
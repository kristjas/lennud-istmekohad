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

    <div v-if="valitudLend">
      <label for="istekohtade-arv">Mitu istekohta:</label>
      <select id="istekohtade-arv" v-model="valitudIstekohtadeArv">
        <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
      </select>

      <label>
        <input type="checkbox" v-model="eelistused.aken" /> Aknakohad
      </label>

      <label for="rida-eelistus">Vali rida:</label>
      <select id="rida-eelistus" v-model="eelistused.rida">
        <option value="suvaline">Suvaline rida</option>
        <option value="esimene">Ekstra jalaruum (esimene rida)</option>
        <option value="viimane">Lähedal väljapääsule (viimane rida)</option>
      </select>

      <button @click="soovitaKohad">Soovita kohad</button>
    </div>


    <div v-if="näitaIstmeid && valitudLend && istmed.length > 0" class="seat-container">
      <h3>Istmed lennuki: {{ valitudLend.lennuk }}</h3>
      <div class="lennukikujund">
        <div class="istmed-grid">
          <div
              v-for="iste in istmed"
              :key="iste.id"
              class="iste"
              :class="{ 'voetud': iste.voetud, 'soovitatud': kasOnSoovitatud(iste) }"
          >
            {{ iste.istekoht }}
          </div>
        </div>
      </div>
    </div>

    <div v-if="näitaIstmeid && valitudLend && istmed.length > 0">
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>istekoht</th>
          <th>Jalaruum</th>
          <th>Aken</th>
          <th>Exit</th>
          <th>Voetud</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="iste in istmed" :key="iste.id">
          <td>{{ iste.id }}</td>
          <td>{{ iste.istekoht }}</td>
          <td>{{ iste.jalaruum ? 'Yes' : 'No' }}</td>
          <td>{{ iste.aken ? 'Yes' : 'No' }}</td>
          <td>{{ iste.exit ? 'Yes' : 'No'  }}</td>
          <td>{{ iste.voetud ? 'Yes' : 'No'  }}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <div v-if="soovitatudIstmed.length > 0">
      <h3>Soovitatud istmed:</h3>
      <p>{{ soovitatudIstmedTekstina }}</p>
    </div>


  </div>


</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { getFlights, getSeatsByLennuk } from "@/api";


const lennud = ref([]);
const valitudLend = ref(null);
const istmed = ref([]);
const valitudIstekohtadeArv = ref(1);
const näitaIstmeid = ref(false);
const soovitatudIstmed = ref([]);
const eelistused = ref({
  aken: false,
  jalaruum: false,
  exit: false,
});


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
  soovitatudIstmed.value = [];
};

const soovitaKohad = () => {
  if (valitudLend.value) {
    soovitatudIstmed.value = leiaSoovitatudIstmed(istmed.value, valitudIstekohtadeArv.value, eelistused.value);
  }
  näitaIstmeid.value = true;
};


const leiaSoovitatudIstmed = (istmed, arv, eelistused) => {

  if (eelistused.rida === "viimane") {
    if (eelistused.aken) {

      return leiaAknakohad(istmed, arv, true);
    } else {

      return leiaIstmed(istmed, arv, true);
    }
  } else {

    if (eelistused.aken) {

      return leiaAknakohad(istmed, arv, false);
    } else {

      return leiaIstmed(istmed, arv, false);
    }
  }
};


const leiaIstmed = (istmed, arv, alatesTagant) => {
  const vabadIstmed = istmed.filter(iste => !iste.voetud);

  vabadIstmed.sort((a, b) => alatesTagant ? b.istekoht - a.istekoht : a.istekoht - b.istekoht);

  for (let i = 0; i <= vabadIstmed.length - arv; i++) {
    const valik = vabadIstmed.slice(i, i + arv);

    let onJarjestikused = true;
    for (let j = 1; j < valik.length; j++) {
      if (alatesTagant) {
        if (valik[j].istekoht !== valik[j - 1].istekoht - 1) {
          onJarjestikused = false;
          break;
        }
      } else {
        if (valik[j].istekoht !== valik[j - 1].istekoht + 1) {
          onJarjestikused = false;
          break;
        }
      }
    }

    if (onJarjestikused) {
      return valik;
    }
  }


  return vabadIstmed.slice(0, arv);
};

const leiaAknakohad = (istmed, arv, alatesTagant) => {
  const vabadIstmed = istmed.filter(iste => !iste.voetud);


  vabadIstmed.sort((a, b) => alatesTagant ? b.istekoht - a.istekoht : a.istekoht - b.istekoht);


  const aknakohad = vabadIstmed.filter(iste => iste.istekoht % 4 === 1 || iste.istekoht % 4 === 0);

  if (aknakohad.length >= arv) {
    return aknakohad.slice(0, arv);
  }

  return vabadIstmed.slice(0, arv);
};


const soovitatudIstmedTekstina = computed(() => {
  if (soovitatudIstmed.value.length === 0) return "Soovitusi ei leitud.";
  return soovitatudIstmed.value.map(iste => `Istekoht ${iste.istekoht}`).join(", ");
});

const kasOnSoovitatud = (iste) => {
  return soovitatudIstmed.value.some(soovitatudIste => soovitatudIste.id === iste.id);
};
</script>


<style>
.lennukikujund {
  width: 300px;
  height: 600px;
  background-color: #e0e0e0;
  border-radius: 150px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  margin: 20px auto;
}

.istmed-grid {
  display: grid;
  grid-template-columns: repeat(4, 50px);
  column-gap: 35px;
  row-gap: 50px;
}

.iste {
  width: 50px;
  height: 50px;
  background-color: #bdbdbd;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 15px;
  cursor: pointer;
  transition: background-color 0.3s;
}


.iste.voetud {
  background-color: #ff4444;
}
.iste.soovitatud {
  background-color: #4CAF50;
  color: white;
}

.iste:hover {
  background-color: #9e9e9e;
}
</style>
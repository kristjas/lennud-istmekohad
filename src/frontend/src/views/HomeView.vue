<template>
  <div class="container">

    <div class="filters">
      <label for="sihtkoht">Vali sihtkoht:</label>
      <select id="sihtkoht" v-model="filtrid.sihtkoht">
        <option value="">Kõik sihtkohad</option>
        <option v-for="sihtkoht in sihtkohad" :key="sihtkoht" :value="sihtkoht">
          {{ sihtkoht }}
        </option>
      </select>

      <label for="date">Vali kuupäev:</label>
      <input id="date" v-model="filtrid.kuupäev" type="date" />

      <label for="kestvus" class="slider">Lennu pikkus:</label>
      <input
          id="kestvus"
          v-model.number="filtrid.kestvus"
          type="range"
          min="1"
          max="5"
          step="1"
          class="slider2"
      />
      <span class="duration-text">{{ filtrid.kestvus }} tundi</span>

      <label for="price" class="slider">Hind:</label>
      <input
          id="price"
          v-model.number="filtrid.hind"
          type="range"
          min="1"
          max="500"
          step="1"
          class="slider2"
      />
      <span class="duration-text">{{ filtrid.hind }} €</span>
    </div>


    <ul class="flight-list">
      <li
          v-for="lend in filtreeritudLennud"
          :key="lend.id"
          @click="valiLend(lend)"
          class="flight-card"
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

    <div class="seat-selection" v-if="valitudLend">
      <label for="istekohtade-arv">Mitu istekohta:</label>
      <select id="istekohtade-arv" v-model="valitudIstekohtadeArv" class="input">
        <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
      </select>

      <label class="checkbox">
        <input type="checkbox" v-model="eelistused.aken" /> Aknakohad
      </label>

      <label for="rida-eelistus">Vali rida:</label>
      <select id="rida-eelistus" v-model="eelistused.rida" class="input">
        <option value="suvaline">Suvaline rida</option>
        <option value="esimene">Ekstra jalaruum (esimene rida)</option>
        <option value="viimane">Lähedal väljapääsule (viimane rida)</option>
      </select>

      <button @click="soovitaKohad" class="btn">Soovita kohad</button>
    </div>


    <div v-if="näitaIstmeid && valitudLend && istmed.length > 0" class="seat-container" ref="lennukikujundRef">
      <h3>Istmed lennukis: {{ valitudLend.lennuk }}</h3>
      <div v-if="soovitatudIstmed.length > 0">
        <h3>Soovitatud istmed:</h3>
        <p>{{ soovitatudIstmedTekstina }}</p>
      </div>
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


  </div>


</template>

<script setup>
import { ref, onMounted, computed, nextTick } from "vue";
import { getFlights, getSeatsByLennuk } from "@/api";


const lennud = ref([]);
const valitudLend = ref(null);
const istmed = ref([]);
const valitudIstekohtadeArv = ref(1);
const näitaIstmeid = ref(false);
const soovitatudIstmed = ref([]);
const lennukikujundRef = ref(null);
const eelistused = ref({
  aken: false,
  jalaruum: false,
  exit: false,
});


const filtrid = ref({
  sihtkoht: '',
  kuupäev: '',
  kestvus: 5,
  hind: 500,
});


//laeme sisse lennud
onMounted(async () => {
  try {
    const data = await getFlights();
    lennud.value = data;
  } catch (error) {
    console.error("Viga lendude laadimisel:", error);
  }
});

//võtame kõik võimalikud sihtkohad
const sihtkohad = computed(() => {
  const kohad = lennud.value.map(lend => lend.sihtkoht);
  return [...new Set(kohad)];
});

//Chatgpt kasutatud - arvutaKestvus
const arvutaKestvus = (startTime, endTime) => {
  if (!startTime || !endTime) return 0;
  const [startHours, startMinutes] = startTime.split(":").map(Number);
  const [endHours, endMinutes] = endTime.split(":").map(Number);
  const kestvus = (endHours * 60 + endMinutes - (startHours * 60 + startMinutes)) / 60;
  return Math.max(kestvus, 0);
};

//vaatame millised lennud sobivad
const filtreeritudLennud = computed(() => {
  return lennud.value.filter(lend => {
    const lennuKestvus = arvutaKestvus(lend.lennualgusaeg, lend.lennulõppaeg);
    return (
        (!filtrid.value.sihtkoht || lend.sihtkoht === filtrid.value.sihtkoht) &&
        (!filtrid.value.kuupäev || lend.kuupäev === filtrid.value.kuupäev) &&
        (!filtrid.value.kestvus || lennuKestvus <= filtrid.value.kestvus) &&
        (!filtrid.value.hind || lend.hind <= filtrid.value.hind)
    );
  });
});

//valiku tegemine
const valiLend = async (lend) => {
  valitudLend.value = lend;
  istmed.value = await getSeatsByLennuk(lend.lennuk);
  soovitatudIstmed.value = [];
};
//leiame mis kohad sobivad ja näitame istmeid
const soovitaKohad = () => {
  if (valitudLend.value) {
    soovitatudIstmed.value = leiaSoovitatudIstmed(istmed.value, valitudIstekohtadeArv.value, eelistused.value);
  }
  näitaIstmeid.value = true;
  nextTick(() => {
    if (lennukikujundRef.value) {
      lennukikujundRef.value.scrollIntoView({ behavior: "smooth" });
    }
  });
};

//vaatame mis viisiga otsime
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

//leiame lihtsalt järjest istmed
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
//Eelistame aknakohti
const leiaAknakohad = (istmed, arv, alatesTagant) => {
  const vabadIstmed = istmed.filter(iste => !iste.voetud);


  vabadIstmed.sort((a, b) => alatesTagant ? b.istekoht - a.istekoht : a.istekoht - b.istekoht);


  const aknakohad = vabadIstmed.filter(iste => iste.istekoht % 4 === 1 || iste.istekoht % 4 === 0);

  if (aknakohad.length >= arv) {
    return aknakohad.slice(0, arv);
  }

  return vabadIstmed.slice(0, arv);
};

//Näitame millised valisime
const soovitatudIstmedTekstina = computed(() => {
  if (soovitatudIstmed.value.length === 0) return "Soovitusi ei leitud.";
  return soovitatudIstmed.value.map(iste => `Istekoht ${iste.istekoht}`).join(", ");
});
//kontrolllime siin kas valitud iste
const kasOnSoovitatud = (iste) => {
  return soovitatudIstmed.value.some(soovitatudIste => soovitatudIste.id === iste.id);
};
</script>


<style scoped>
.container {
  max-width: 1200px;
  margin: auto;
  font-family: 'Arial', sans-serif;
  background-color: #ffffff;
  color: #333;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 30px;
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.filters label {
  font-weight: 600;
  color: #555;
}

.input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  width: 100%;
  max-width: 200px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.input:focus {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.2);
  outline: none;
}

.slider {
  width: 100%;
  max-width: 80px;
}
.slider2 {
  accent-color: #333333;
}

.duration-text {
  font-weight: bold;
  margin-left: 5px;
  color: rgb(88, 93, 96);
}

.flight-list {
  list-style-type: none;
  padding: 0;
}

.flight-card {
  background: white;
  padding: 20px;
  margin: 15px 0;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  border-left: 4px solid #000000;
}

.flight-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.seat-selection {
  margin-top: 30px;
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.checkbox input {
  margin: 0;
}

.btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s, transform 0.2s;
  font-weight: 600;
}

.btn:hover {
  background-color: #0056b3;
  transform: translateY(-1px);
}

.seat-container {
  margin-top: 30px;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

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

h3 {
  color: #007bff;
  margin-bottom: 15px;
}

p {
  color: #555;
  line-height: 1.6;
}
</style>
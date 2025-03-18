<template>
  <div>
    <h2>Available Flights</h2>
    <ul>
      <li v-for="flight in flights" :key="flight.id">
        <strong>{{ flight.sihtkoht }}</strong> ({{ flight.kuupäev }})
        - Starts at: {{ flight.lennualgusaeg }} | Ends at: {{ flight.lennulõppaeg }} 
        - Price: €{{ flight.hind }} | Aircraft: {{ flight.lennuk }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getFlights } from "@/api";

const flights = ref([]);

onMounted(async () => {
  flights.value = await getFlights();
});
</script>

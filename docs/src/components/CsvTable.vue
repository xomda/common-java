<template>
    <table>
        <tr v-for="(row, i) in data">
            <td v-for="n in cols">
                {{ row[n-1] ?? "" }}
            </td>
        </tr>
    </table>
</template>

<script setup>
const props = defineProps({  data: String, autoprune: {type:Boolean, default: true} })
const data = (props.data ?? "")
    .split("\n")
    .filter(Boolean)
    .map(r => r.trim().split(";"))
let cols = Math.max(...data.map(r => r.length))
if(props.autoprune) while( data.every(row => row[cols-1] === "") ){
    --cols;
}
</script>

<template>
  <ErrorBoundary>
    <router-view />
  </ErrorBoundary>
</template>

<script setup>
import ErrorBoundary from './components/ErrorBoundary.vue'
</script>

<style>
#app {
  width: 100%;
  min-height: 100vh;
}
</style>

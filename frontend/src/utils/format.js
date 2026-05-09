export const formatPercent = (value, decimals = 2) => {
  const number = Number(value)
  if (Number.isNaN(number)) return '0'
  return number.toFixed(decimals)
}

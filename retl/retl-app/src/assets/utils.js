export function copyData(tar, src) {
  if (src === null || src === undefined) {
    return;
  }
  if (tar === null || tar === undefined) {
    tar = {}
  }
  Object.keys(src).forEach(field => {
    tar[field] = src[field]
  })
}

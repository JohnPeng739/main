export default function ({store, app, error}) {
  if (!store.getters['account/authenticated']) {
    error({
      message: app.i18n.t('nav.message.nonAuthority'),
      statusCode: 403
    })
  }
}

import locale from '@/assets/lang'
import ajax from '@/utils/ajax'
import notify from '@/utils/notify'
import formValidateRules from '@/utils/form-validate-rules'
import echartsUtils from '@/utils/echarts.js'

import Icon from '@/components/icon.vue'
import PaginatePane from '@/components/paginate-pane.vue'

import ChooseInput from '@/components/form/choose-input.vue'
import ChooseTag from '@/components/form/choose-tag.vue'

import TagNormal from '@/components/form/tag-normal.vue'
import TagCouple from '@/components/form/tag-couple.vue'

import DialogPane from '@/components/dialog-pane.vue'

import LayoutNormal from '@/layout/normal/index.vue'

export {locale, ajax, notify, formValidateRules, echartsUtils,
  Icon, PaginatePane,
  ChooseInput, ChooseTag,
  TagNormal, TagCouple,
  DialogPane,
  LayoutNormal
}

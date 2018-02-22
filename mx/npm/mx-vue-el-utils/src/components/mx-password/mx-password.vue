<template>
  <el-input :type="type" v-model="password" :maxlength="maxlength" :minlength="minlength" :placeholder="placeholder"
            :disabled="disabled" :size="size" :readonly="readonly" :autofocus="autofocus" :tabindex="tabindex">
    <mx-icon :name="iconName" class="password" slot="suffix" @click.native="handleClickSwitch" :disabled="disabled"></mx-icon>
  </el-input>
</template>

<script>
  export default {
    name: 'mx-password',
    props: {
      value: {
        type: String,
        default: ''
      },
      maxlength: {
        type: Number
      },
      minlength: {
        type: Number
      },
      placeholder: {
        type: String
      },
      disabled: {
        type: Boolean,
        default: false
      },
      size: {
        type: String
      },
      readonly: {
        type: Boolean,
        default: false
      },
      autofocus: {
        type: Boolean,
        default: false
      },
      tabindex: {
        type: String
      }
    },
    data () {
      return {
        visibility: false
      }
    },
    computed: {
      password: {
        get () {
          return this.value
        },
        set (val) {
          this.$emit('input', val)
        }
      },
      type () {
        return this.visibility ? 'text' : 'password'
      },
      iconName () {
        return this.visibility ? 'visibility' : 'visibility_off'
      }
    },
    methods: {
      handleClickSwitch () {
        if (this.disabled === false) {
          this.visibility = !this.visibility
        }
      }
    }
  }
</script>

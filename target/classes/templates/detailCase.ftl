<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <!-- 1. 引入vue -->
    <script src="https://cdn.jsdelivr.net/npm/vue@2"></script>
    <!-- 2. 引入样式 -->
    <link
            rel="stylesheet"
            href="https://unpkg.com/element-ui/lib/theme-chalk/index.css"
    />
    <!-- 3. 引入组件库 -->
    <script src="https://unpkg.com/element-ui/lib/index.js"></script>
    <title>element</title>
</head>

<body>
<div id="app">
    <div class="title">
        就诊者详细病例
    </div>
    <div class="detailCase">
        <el-form ref="form" :model="form" label-width="20%">
            <el-form-item label="就诊者姓名" >
                <el-input v-model="form.patientName" disabled>  </el-input>
            </el-form-item>
            <el-form-item label="就诊者性别">
                <el-input v-model="form.patientSex" disabled></el-input>
            </el-form-item>
            <el-form-item label="就诊者年龄">
                <el-input v-model="form.patientAge" disabled></el-input>
            </el-form-item>
            <el-form-item label="医生姓名">
                <el-input v-model="form.doctorName" disabled></el-input>
            </el-form-item>
            <el-form-item label="简略病例">
                <el-input v-model="form.simpleCause" disabled></el-input>
            </el-form-item>
            <el-form-item label="详细病例">
                <el-input type="textarea" v-model="form.detailCause" disabled></el-input>
            </el-form-item>
            <el-form-item  label="病例图片" prop="causeImages">
                <div class="images">
                    <div class="block" v-for="image in form.causeImages" :key="image">
                        <el-image
                                style="width: 150px; height: 150px"
                                :src="image"
                                fit="fill"></el-image>
                    </div>
                </div>

            </el-form-item>
        </el-form>
    </div>
</div>
</body>
<script>
    new Vue({
        el: '#app',
        data() {
            return {
                form: {
                    patientName: '你好',
                    patientSex: '',
                    patientAge: '',
                    doctorName: '',
                    simpleCause: '',
                    detailCause: '',
                    causeImages: []
                }
            };
        },
        methods: {
            init(){

                var info ="${detailPatientInfo.causeImages?json_string}"
                console.log("检查是否正常",info)
                this.form.patientName = "${detailPatientInfo.patientName?js_string}";
                this.form.patientSex = "${detailPatientInfo.patientSex?js_string}";
                this.form.patientAge = "${detailPatientInfo.patientAge?js_string}";
                this.form.doctorName = "${detailPatientInfo.doctorName?js_string}";
                this.form.simpleCause = "${detailPatientInfo.simpleCause?js_string}";
                this.form.detailCause = "${detailPatientInfo.detailCause?js_string}";
                this.form.causeImages = JSON.parse("${detailPatientInfo.causeImages?js_string}");

            }

        },
        created(){
            this.init();
        }
    });
</script>
<style>
    body{
        margin: 0;
        padding:0;
        height: 100%;
        box-sizing: border-box;
        background-color: #f4f4f4;
    }
    #app {
        height: 100%;
        width: 70%;
        display: flex;
        flex-direction: column;
        padding-left: 15%;
    }
    .title{
        align-items: center;
        padding-top: 10%;
        padding-bottom: 3%;
        padding-left: 25%;
        height: 5%;
        font-size: 50px;
        font-family: "楷体";
        background-color: white;
    }

    .detailCase{
        background-color: white;
        padding-right: 25%;
    }
    .block{
        padding-right: 10px;
    }
    .images{
        display: flex;
    }



</style>
</html>


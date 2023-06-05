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
            href="https://unpkg.com/vant@2.12/lib/index.css"
    />
    <!-- 3. 引入组件库 -->
    <script src="https://unpkg.com/vant@2.12/lib/vant.min.js"></script>
    <title>element</title>
</head>

<body>
<div id="app">
    <div class="title">
        就诊者简略病例
    </div>
    <div class="simpleCase">
        <van-list
                v-model="loading"
                :finished="finished"
                finished-text=""
                @load="onLoad"
                class="patientInfo"
        >
            <van-cell class="inputValue">
                <div class="content">就诊者姓名: &emsp; ${simplePatientInfo.patientName}</div>
            </van-cell>
            <van-cell class="inputValue">
                <div class="content">就诊者性别: &emsp; ${simplePatientInfo.patientSex}</div>
            </van-cell>
            <van-cell class="inputValue">
                <div class="content">就诊者年龄: &emsp; ${simplePatientInfo.patientAge}</div>
            </van-cell>
            <van-cell class="inputValue">
                <div class="content">问诊医生: &emsp;&emsp; ${simplePatientInfo.doctorName}</div>
            </van-cell>
            <van-cell class="caseInfo">
                <div class="content">简略病因: &emsp;&emsp; ${simplePatientInfo.simpleCause}</div>
            </van-cell>
        </van-list>
    </div>
</div>
</body>
<script>
    new Vue({
        el: '#app',
        data() {
            return {
                loading: false,
                finished: true,
                simplePatientInfo: {
                    patientName: '姓名',
                    patientSex: '性别',
                    patientAge: '年龄',
                    doctorName: '医生姓名',
                    simpleCause: '简略病因'
                }

            };
        },
        methods: {
            onLoad(){

            }

        },
        created(){
        }
    });
</script>
<style>
    body{
        margin: 0;
        padding:0;
        height: 166.667vw;
        box-sizing: border-box;
        background-color: #f4f4f4;
    }
    #app {
        height: 166.667vw;
        width: 100%;
        display: flex;
        flex-direction: column;
    }
    .title{
        align-items: center;
        padding-top: 10%;
        padding-bottom: 10%;
        padding-left: 25%;
        height: 5%;
        font-size: 25px;
        font-family: "楷体";
        background-color: white;
    }
    .simpleCase{
        height: 90%;

    }
    .patientInfo{
        height: 70%;
    }
    .inputValue{
        font-size: 20px;
        height: 20%;
        font-family: "黑体";
    }

    .caseInfo{
        font-size: 20px;
        height: 10%;
        font-family: "黑体";
        height: 20%;
    }
    .content{
        height: 100%;
    }

</style>
</html>


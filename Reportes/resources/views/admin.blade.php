<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Admin</title>
    <style>
        .main-container {
            background-color: #ffffff;
            border: 1px solid;
            box-shadow: 5px 10px #888888;
            border-radius: 25px;
            padding: 0px;
            height: 80vh;
            width: 150vh;
            /* Altura del 100% de la ventana (viewport height) */
            margin: 0;
            /* Eliminar márgenes para ocupar todo el espacio */
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .inner-container {
            display: flex;
            flex-direction: column;
        }

        .row {
            display: flex;
            justify-content: space-between;
            /* Añade espacio entre los elementos en la fila */
        }

        .box {
            background-color: #2a2d30;
            border: 1px solid;
            box-shadow: 5px 10px #888888;
            border-radius: 25px;
            padding: 15px;
            width: 48%;
            min-height: 200px;
            min-width: 300px;
            /* Ajusta el ancho para que ambas cajas encajen en la misma fila */
        }

        .exit-button {
            margin-top: 15px;
        }
    </style>
</head>

<body
    style="background-color: #b9b9b9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0;">
    <div class="container">
        <div class="main-container">
            <div class="inner-container" style="display: flex; flex-direction: column; align-items: center;">
                <div class="row">
                    <div class="col-md-6">

                        <div class="box" style="width: auto; margin: 0 auto; text-align: center; margin-right: 20px">
                            <form action="{{ route('reporte.pdf') }}" method="POST">
                                @csrf
                                <h4
                                    style="color:#ffffff; font-size:23px; font-weight:bold; animation:fadeIn 1s Fade-In-Out; margin: 0; padding-top: 10px;">
                                    Sales report</h4>
                                <div class="row">
                                    <div class="col-sm-6"
                                        style="margin-top: 10px; margin-right: 25px; margin-left: 10px">
                                        <div class="form-group" style="margin-bottom: 20px;">
                                            <h5 style="color:#fff; font-size:17px;">Start date</h5>
                                            <input type="date" name="start" value="{{ $data['min'] }}"
                                                max="{{ $data['min'] }}" class="form-control" style="width: 100%;">
                                        </div>
                                    </div>
                                    <div class="col-sm-6"
                                        style="margin-top: 10px; margin-left: 25px; margin-right: 10px">
                                        <div class="form-group" style="margin-bottom: 20px;">
                                            <h5 style="color:#fff; font-size:17px;">Final date</h5>
                                            <input type="date" name="end" value="{{ $data['max'] }}"
                                                max="{{ $data['max'] }}" class="form-control" style="width: 100%;">
                                        </div>
                                    </div>
                                </div>
                                <div style="margin-top: 10px;">
                                    <input type="submit" value="Search" class="btn"
                                        style="background-color: #3498db; color: #ffffff; border: none; padding: 6px 10px; border-radius: 5px; cursor: pointer; width: 30%; display: block; margin: 0 auto;">
                                </div>
                            </form>
                        </div>





                    </div>
                    <div class="col-md-6">
                        <form action="{{ route('stock.pdf') }}" method="POST">
                            @csrf
                            <div class="box"
                                style="width: auto; margin: 0 auto; text-align: center; margin-left: 20px">
                                <h4
                                    style="color:#ffffff; font-size:23px; font-weight:bold; animation:fadeIn 1s Fade-In-Out; margin: 0; padding-top: 10px;">
                                    Product Details
                                </h4>

                                <div class="row" style="margin-top: 30px;">
                                    <div
                                        style="display: flex; align-items: center; justify-content: space-between; width: 100%;">
                                        <h5 style="color:#fff; font-size:17px; margin-right: 10px;">Product:</h5>
                                        <div style="flex: 1; display: flex; justify-content: flex-end;">
                                            <select name="product" class="form-control form-control-sm">
                                                <option value="-1">All products</option>
                                                @foreach ($products as $item)
                                                    <option value="{{ $item->id }}">{{ $item->name }}</option>
                                                @endforeach
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div style="margin-top: 25px;">
                                    <input type="submit" value="Search" class="btn"
                                        style="background-color: #3498db; color: #ffffff; border: none; padding: 6px 10px; border-radius: 5px; cursor: pointer; width: 30%; display: block; margin: 0 auto;">
                                </div>
                            </div>
                        </form>
                    </div>

                </div>

                <div class="exit-button text-center" style="margin-top: 20px;">
                    <form action="{{ route('logout') }}">
                        @csrf
                        <input type="submit" value="Exit" class="btn"
                            style="background-color: #ff0000; color: #ffffff; border-radius: 5px; font-size: 20px; padding: 6px 20px; margin-top: 50%">
                    </form>
                </div>


            </div>
        </div>
    </div>

</body>

</html>
